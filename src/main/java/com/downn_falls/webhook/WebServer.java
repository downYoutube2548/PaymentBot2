package com.downn_falls.webhook;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionListLineItemsParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import com.downn_falls.PaymentBot;
import com.downn_falls.manager.YamlManager;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static spark.Spark.*;

public class WebServer {

    public static void start() {
        secure(YamlManager.getConfig("web-server.certificate", String.class), YamlManager.getConfig("web-server.keystore-password", String.class), null, null);
        port(YamlManager.getConfig("web-server.port", Integer.class));
        post(YamlManager.getConfig("web-server.path", String.class), (request, response) -> {
            try {
                handle(request, response);
            } catch (StripeException e) {
                e.printStackTrace();
                response.status(500);
                return "Error processing webhook";
            }
            return "";
        });
    }

    public static void handle(Request request, Response response) throws StripeException, SQLException {
        String payload = request.body();
        String sigHeader = request.headers("Stripe-Signature");
        Event event;

        String endpointSecret = PaymentBot.testMode ? YamlManager.getConfig("endpoint-secret-test-key", String.class) : YamlManager.getConfig("endpoint-secret-live-key", String.class); // test: whsec_BdoXgd1wbHDWavOpkVeWD9YZU27dbl0W // real: whsec_leKkVhSN9j4BzFijISxO52Bxm6hJm7Ch

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            response.status(400);
            return;
        }

        if ("checkout.session.completed".equals(event.getType())) {

            Session sessionEvent = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (sessionEvent == null) {
                response.status(400);
                return;
            }

            SessionRetrieveParams params =
                    SessionRetrieveParams.builder()
                            .addExpand("line_items")
                            .build();

            Session session = Session.retrieve(sessionEvent.getId(), params, null);

            SessionListLineItemsParams listLineItemsParams =
                    SessionListLineItemsParams.builder()
                            .build();

            LineItemCollection lineItems = session.listLineItems(listLineItemsParams);
            fulfillOrder(lineItems);
        }

        response.status(200);
    }

    public static void fulfillOrder(LineItemCollection lineItems) throws SQLException {
        System.out.println("Fulfilling order...");
        UUID uuid = UUID.fromString(lineItems.getData().get(0).getPrice().getMetadata().get("order_id"));
        long orderCreateTimestamp = Long.parseLong(lineItems.getData().get(0).getPrice().getMetadata().get("order_create_timestamp"));
        double amount = lineItems.getData().get(0).getPrice().getUnitAmount() / 100.0;
        StringSelectInteractionEvent event = PaymentBot.sessionMap.get(uuid);

        String username = event.getUser().getName();
        String nickname = event.getMember().getNickname();

        PaymentBot.databaseManager.load(event.getGuild().getId(), event.getUser().getId(), (balanceData) -> {

            balanceData.setGuildName(event.getGuild().getName()).setUsername(event.getUser().getName());

            balanceData.add(amount);

            MessageEmbed embed = new MessageEmbed(
                    null,
                    "ทำรายการสำเร็จแล้ว!!",
                    null,
                    EmbedType.UNKNOWN,
                    null,
                    65378,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new ArrayList<>()
            );

            MessageEmbed embed2 = new MessageEmbed(
                    null,
                    "บันทึกรายการ",
                    "\u200e",
                    EmbedType.UNKNOWN,
                    OffsetDateTime.now(),
                    7711487,
                    null,
                    null,
                    null,
                    null,
                    new MessageEmbed.Footer(username, event.getUser().getAvatarUrl(), null),
                    null,
                    List.of(
                            new MessageEmbed.Field("ชื่อลูกค้า:", nickname + " ("+ username +")", false),
                            new MessageEmbed.Field("สร้างรายการเมื่อ:", "<t:"+orderCreateTimestamp/1000+":F>", false),
                            new MessageEmbed.Field("จำนวนเงิน:", amount+" บาท (คงเหลือ: "+ balanceData.getBalance()+")", false),
                            new MessageEmbed.Field("ทำรายการสำเร็จเมื่อ:", "<t:"+System.currentTimeMillis()/1000+":F>\n\u200e", false)
                    )
            );


            event.getHook().editOriginalEmbeds(embed).setComponents().queue();
            event.getChannel().asGuildMessageChannel().sendMessageEmbeds(embed2).queue();

        });

        PaymentBot.sessionMap.remove(uuid);
    }
}

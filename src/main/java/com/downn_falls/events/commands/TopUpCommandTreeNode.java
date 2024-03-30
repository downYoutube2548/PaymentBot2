package com.downn_falls.events.commands;

import com.downn_falls.PaymentBot;
import com.downn_falls.manager.YamlManager;
import com.downn_falls.utils.Utils;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TopUpCommandTreeNode extends CommandTreeNode {

    public TopUpCommandTreeNode(CommandTreeNode parent) {
        super(parent, "topup");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {

        event.deferReply().setEphemeral(true).queue();

        UUID orderId = UUID.randomUUID();
        double product_price = event.getOption("price").getAsDouble();

        Product product;
        ProductCreateParams product_params = ProductCreateParams.builder()
                .setName(event.getGuild().getName())
                .build();

        try {
            product = Product.create(product_params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        Price price = null;
        if (product != null) {
            PriceCreateParams price_params =
                    PriceCreateParams.builder()
                            .setProduct(product.getId())
                            .setUnitAmount((long) (product_price * 100))
                            .putMetadata("order_id", orderId.toString())
                            .putMetadata("order_create_timestamp", String.valueOf(System.currentTimeMillis()))
                            .setCurrency("thb")
                            .build();

            try {
                price = Price.create(price_params);
            } catch (StripeException e) {
                throw new RuntimeException(e);
            }
        }

        Session session = null;
        if (price != null) {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setSuccessUrl(YamlManager.getConfig("web-server.success-url", String.class))
                            .setCancelUrl(YamlManager.getConfig("web-server.cancel-url", String.class))
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPrice(price.getId())
                                            .build()
                            )
                            .setMode(SessionCreateParams.Mode.PAYMENT).build();
            try {
                session = Session.create(params);

            } catch (StripeException e) {
                throw new RuntimeException(e);
            }
        }

        if (session != null) {

            event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setTitle("กรุณากดปุ่มด้านล่างเพื่อชำระเงิน")
                            .setDescription("\u200e")
                            .setColor(16760463)
                            .setTimestamp(OffsetDateTime.now())
                            .setFooter(PaymentBot.testMode ? "Test mode enabled" : null, null)
                            .addField("ราคา (Price):", event.getOption("price").getAsDouble()+" บาท\n\u200e", false)
                            .build()
                    )
                    .setActionRow(Button.link(session.getUrl(), "ชำระเงิน"))
                    .queue();

            PaymentBot.sessionMap.put(orderId, event);

        } else {
            event.getHook().editOriginalEmbeds(Utils.errorEmbed("เกิดข้อผิดพลาด")).queue();
        }
    }
}

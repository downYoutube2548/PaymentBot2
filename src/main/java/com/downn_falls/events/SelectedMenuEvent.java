package com.downn_falls.events;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.downn_falls.PaymentBot;

import java.util.ArrayList;
import java.util.UUID;

public class SelectedMenuEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        event.getInteraction().deferReply(true).queue();

        UUID orderId = UUID.randomUUID();

        if (event.getComponentId().startsWith("payment_method")) {

            double product_price = Double.parseDouble(event.getComponentId().split(";")[1]);

            if (event.getValues().get(0).equals("promptpay")) {

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
                                    .setSuccessUrl("http://140.99.98.15/payment_success.html")
                                    .setCancelUrl("http://140.99.98.15/payment_cancel.html")
                                    .addLineItem(
                                            SessionCreateParams.LineItem.builder()
                                                    .setQuantity(1L)
                                                    .setPrice(price.getId())
                                                    .build()
                                    )
                                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PROMPTPAY)
                                    .setMode(SessionCreateParams.Mode.PAYMENT).build();
                    try {
                        session = Session.create(params);

                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (session != null) {

                    MessageEmbed embed = new MessageEmbed(
                            null,
                            "กรุณากดปุ่มด้านล่างเพื่อชำระเงิน",
                            null,
                            EmbedType.UNKNOWN,
                            null,
                            16760463,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            new ArrayList<>()
                    );

                    event.getHook().editOriginalEmbeds(embed).setActionRow(
                            Button.link(session.getUrl(), "ชำระเงิน")
                    ).queue();

                    PaymentBot.sessionMap.put(orderId, event);

                } else {

                    MessageEmbed embed = new MessageEmbed(
                            null,
                            "เกิดข้อผิดพลาด",
                            null,
                            EmbedType.UNKNOWN,
                            null,
                            16741235,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            new ArrayList<>()
                    );

                    event.getHook().editOriginalEmbeds(embed).queue();
                }



            }
        }
    }
}

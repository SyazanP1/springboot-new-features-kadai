const stripe = Stripe('pk_test_51R2UtSFfHVELjUI2IhNDC90U65xs9YWiMlgsBhcozyyqBbithRozYIoE0uzOj6J3ipPQpCYoTOJcEHUoDsvnHfeV00AJTXtNm4');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
	stripe.redirectToCheckout({
		sessionId: sessionId
	})
});
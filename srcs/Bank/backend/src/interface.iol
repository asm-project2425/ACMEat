type loginRequest: void {
	.username: string
	.password: string
}

type loginResponse: void {
	.success: bool
	.sessionId?: string
}

type createPaymentRequest: void {
	.sessionId: string
	.amount: double
	.orderId: int
}

type createPaymentResponse: void {
	.success: bool
	.paymentId?: int
}

type completePaymentRequest: void {
	.sessionId: string
	.paymentId: int
}

type completePaymentResponse: void {
	.success: bool
	.token?: string
}

type verifyTokenRequest: void {
	.sessionId: string
	.token: string
}

type verifyTokenResponse: void {
	.success: bool
}

type refundRequest: void {
	.sessionId: string
	.paymentId: int
}

type refundResponse: void {
	.success: bool
}

type confirmRequest: void {
	.sessionId: string
	.paymentId: int
}

type confirmResponse: void {
	.success: bool
}

type logoutRequest: void {
	.sessionId: string
}

interface BankInterface {
    RequestResponse:
		login( loginRequest )( loginResponse ),
		createPayment( createPaymentRequest )( createPaymentResponse ),
		completePayment( completePaymentRequest )( completePaymentResponse ),
		verifyToken( verifyTokenRequest )( verifyTokenResponse ),
		refund( refundRequest )( refundResponse ),
		confirm( confirmRequest )( confirmResponse )
	OneWay:
		logout( logoutRequest )
}
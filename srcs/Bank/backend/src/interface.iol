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

type refundRequest: void {
	.sessionId: string
	.paymentId: int
}

type confirmRequest: void {
	.sessionId: string
	.paymentId: int
}

type successResponse: void {
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
		verifyToken( verifyTokenRequest )( successResponse ),
		refund( refundRequest )( successResponse ),
		confirm( confirmRequest )( successResponse )
	OneWay:
		logout( logoutRequest )
}
type loginRequest: void {
	.username: string
	.password: string
}

type loginResponse: void {
	.success: bool
	.sid?: string
}

type payRequest: void {
	.sid: string
	.bill: double
  	.orderId: int
}

type payResponse: void {
	.success: bool
	.token?: string
}

type verifyTokenRequest: void {
	.sid: string
	.token: string
	.orderId: int
}

type confirmRequest: void {
	.sid: string
	.orderId: int
}

type refundRequest: void {
	.sid: string
	.orderId: int
}

type successResponse: void {
	.success: bool
}

type logoutRequest: void {
	.sid: string
}

interface BankInterface {
    RequestResponse:
		login( loginRequest )( loginResponse ),
		pay( payRequest )( payResponse ),
		verifyToken( verifyTokenRequest )( successResponse ),
		refund( refundRequest )( successResponse ),
		confirm( confirmRequest )( successResponse )
	OneWay:
		logout( logoutRequest )
}
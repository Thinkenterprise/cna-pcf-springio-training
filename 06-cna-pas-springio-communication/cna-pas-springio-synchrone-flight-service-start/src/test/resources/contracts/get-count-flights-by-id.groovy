import org.springframework.cloud.contract.spec.Contract

Contract.make {
	description "should return 1 by id=101"

	request {
		url "/flights/count/"
		method GET()
	}

	response {
		status OK()
		body (
		)
	}
}
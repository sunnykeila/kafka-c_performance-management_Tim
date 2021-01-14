package com.course.microservice.command.service;

import com.course.microservice.broker.message.*;
import com.course.microservice.broker.publisher.OrchestrationSagaPublisher;
import com.course.microservice.entity.PerformanceAppraisalStatus;
import com.course.microservice.repository.PerformanceAppraisalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrchestrationPerformanceAppraisalSaga {

	private static final Logger LOG = LoggerFactory.getLogger(OrchestrationPerformanceAppraisalSaga.class);

	@Autowired
	private OrchestrationSagaPublisher publisher;

	@Autowired
	private PerformanceAppraisalRepository repository;

	public void approvePerformanceAppraisal(String appraisalId) {

		// UUID.fromString(appraisalId)    <-- as in network the   UUID  become String type so need to change the type to     UUID
		var appraisal = repository.findById(UUID.fromString(appraisalId)).orElseThrow();  // if no record found with id then throw exception

		if (appraisal.isFinalState()) {  return;  }   // if FinalState = APPROVED or APPROVED_ERROR  then return the control from here
                                                        // do not execute the code further

		LOG.debug("[Orchestration-Saga] Approving performance appraisal");

		// 1. as we are starting the call from here to other microservices, so we are changing the status from  "NEW"  to  "APPROVAL_ON_PROGRESS"
		repository.updatePerformanceAppraisalStatusById(PerformanceAppraisalStatus.APPROVAL_ON_PROGRESS.toString(),  UUID.fromString(appraisalId));

		// 2. publish 'pay bonus message' to kafka
		var payBonusMessage = new PayBonusMessage();
		payBonusMessage.setAppraisalId(appraisalId);
		payBonusMessage.setEmployeeId(appraisal.getEmployeeId());
		payBonusMessage.setGrade(appraisal.getGrade());
		payBonusMessage.setScore(appraisal.getScore());

		publisher.publishToBonusPayment(payBonusMessage);
	}

	@KafkaListener(topics = "t.saga03.payrollcompensation.response")
	public void listenBonusPaymentResponse(BonusPaidMessage message) throws InterruptedException {
		LOG.debug("[Orchestration-Saga] Listening bonus paid message for appraisal {}", message.getAppraisalId());

		// ...
		// do business logic here for bonus paid message, e.g. check no error, and
		// acknowledge / log payment
		// ...

		// simulate process
		Thread.sleep(2000);

		// send the message to next topic in saga
		var recordBonusMessage = new RecordBonusMessage();
		recordBonusMessage.setAppraisalId(message.getAppraisalId());
		recordBonusMessage.setEmployeeId(message.getEmployeeId());
		recordBonusMessage.setBonusPaidDateTime(message.getBonusPaidDateTime());
		recordBonusMessage.setBonusAmount(message.getBonusAmount());

		LOG.debug("[Orchestration-Saga] Publishing to record bonus request topic for appraisal {}",  recordBonusMessage.getAppraisalId());

		publisher.publishToRecordBonus(recordBonusMessage);
	}

	@KafkaListener(topics = "t.saga03.organizationdevelopment.response")
	public void listenBonusRecordResponse(BonusRecordedMessage message) throws InterruptedException {

		LOG.debug("[Orchestration-Saga] Listening bonus recorded message for appraisal {}", message.getAppraisalId());

		// simulate finalization process
		Thread.sleep(2000);

		// send the message to next topic in saga
		var finalizeAppraisalMessage = new FinalizeAppraisalMessage();
		finalizeAppraisalMessage.setAppraisalId(message.getAppraisalId());
		finalizeAppraisalMessage.setBonusRecordedDateTime(message.getBonusRecordedDateTime());

		LOG.debug("[Orchestration-Saga] Publishing to finalize appraisal request topic for appraisal {}", finalizeAppraisalMessage.getAppraisalId());

		publisher.publishToFinalizeAppraisal(finalizeAppraisalMessage);

	}

}

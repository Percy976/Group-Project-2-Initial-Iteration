package com.marketplace.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a customer's request for a listed service.
 *
 * Related requirements: PR0013 through PR0018.
 * COMPLETED supports review eligibility under PR0019.
 */
public class ServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Represents Status in the marketplace application.
     */
    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED,
        CANCELLED,
        COMPLETED
    }

    private int requestId;
    private int listingId;
    private int customerId;
    private int providerId;
    private String jobDetails;
    private String submissionMessage;
    private LocalDateTime submissionDate;
    private Status status;
    private String responseMessage;

    /**
     * Creates a new ServiceRequest instance.
     */
    public ServiceRequest() {
        this.status = Status.PENDING;
        this.responseMessage = "";
    }

    /**
     * Creates a new ServiceRequest instance.
     * @param requestId the request id
     * @param listingId the listing id
     * @param customerId the customer id
     * @param providerId the provider id
     * @param jobDetails the job details
     * @param submissionMessage the submission message
     * @param submissionDate the submission date
     * @param status the status
     * @param responseMessage the response message
     */
    public ServiceRequest(int requestId, int listingId,
                          int customerId, int providerId,
                          String jobDetails, String submissionMessage,
                          LocalDateTime submissionDate, Status status,
                          String responseMessage) {
        this.requestId = requestId;
        this.listingId = listingId;
        this.customerId = customerId;
        this.providerId = providerId;
        this.jobDetails = jobDetails;
        this.submissionMessage = submissionMessage;
        this.submissionDate = submissionDate;
        this.status = status;
        this.responseMessage = responseMessage;
    }

    /**
     * Returns the request id.
     * @return the current request id
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Sets the request id.
     * @param requestId the request id
     */
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns the listing id.
     * @return the current listing id
     */
    public int getListingId() {
        return listingId;
    }

    /**
     * Sets the listing id.
     * @param listingId the listing id
     */
    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    /**
     * Returns the customer id.
     * @return the current customer id
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer id.
     * @param customerId the customer id
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the provider id.
     * @return the current provider id
     */
    public int getProviderId() {
        return providerId;
    }

    /**
     * Sets the provider id.
     * @param providerId the provider id
     */
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    /**
     * Returns the job details.
     * @return the current job details
     */
    public String getJobDetails() {
        return jobDetails;
    }

    /**
     * Sets the job details.
     * @param jobDetails the job details
     */
    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    /**
     * Returns the submission message.
     * @return the current submission message
     */
    public String getSubmissionMessage() {
        return submissionMessage;
    }

    /**
     * Sets the submission message.
     * @param submissionMessage the submission message
     */
    public void setSubmissionMessage(String submissionMessage) {
        this.submissionMessage = submissionMessage;
    }

    /**
     * Returns the submission date.
     * @return the current submission date
     */
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Sets the submission date.
     * @param submissionDate the submission date
     */
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * Returns the status.
     * @return the current status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status the status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Returns the response message.
     * @return the current response message
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Sets the response message.
     * @param responseMessage the response message
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
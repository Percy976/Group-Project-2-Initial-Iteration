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

    public ServiceRequest() {
        this.status = Status.PENDING;
        this.responseMessage = "";
    }

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

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    public String getSubmissionMessage() {
        return submissionMessage;
    }

    public void setSubmissionMessage(String submissionMessage) {
        this.submissionMessage = submissionMessage;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
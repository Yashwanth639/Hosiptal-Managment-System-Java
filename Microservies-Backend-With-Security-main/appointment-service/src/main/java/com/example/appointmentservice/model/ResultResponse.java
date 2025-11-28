package com.example.appointmentservice.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * Generic response object to encapsulate the result of an API call.
 *
 * @param <T> The type of data contained in the response.
 */

@Data
@Builder
public class ResultResponse<T> {

	/**
	 * Indicates whether the API call was successful.
	 */
	private boolean success;
	/**
	 * A message describing the result of the API call.
	 */
	private String message;
	/**
	 * The data returned by the API call.
	 */
	private T data;
	/**
	 * The timestamp of when the response was generated.
	 */
	private LocalDateTime timestamp;

}

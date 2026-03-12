package com.telecom.entity;

import jakarta.persistence.*;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private int validityDays;
    private int dataLimitGB;
    private double speedMbps;

    @ManyToOne
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getValidityDays() {
		return validityDays;
	}

	public void setValidityDays(int validityDays) {
		this.validityDays = validityDays;
	}

	public int getDataLimitGB() {
		return dataLimitGB;
	}

	public void setDataLimitGB(int dataLimitGB) {
		this.dataLimitGB = dataLimitGB;
	}

	public double getSpeedMbps() {
		return speedMbps;
	}

	public void setSpeedMbps(double speedMbps) {
		this.speedMbps = speedMbps;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

    // getters and setters
    
}

package com.telecom.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Operator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String coverageRating;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL)
    private List<Plan> plans;

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

	public String getCoverageRating() {
		return coverageRating;
	}

	public void setCoverageRating(String coverageRating) {
		this.coverageRating = coverageRating;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

    // getters and setters
    
}
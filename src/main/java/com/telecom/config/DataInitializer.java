package com.telecom.config;

import com.telecom.model.entity.Plan;
import com.telecom.model.entity.User;
import com.telecom.repository.PlanRepository;
import com.telecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedPlans();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        userRepository.saveAll(List.of(
            User.builder()
                .username("admin")
                .email("admin@telecom.com")
                .password(passwordEncoder.encode("admin123"))
                .roles(Set.of(User.Role.ROLE_ADMIN, User.Role.ROLE_USER))
                .enabled(true).build(),
            User.builder()
                .username("john")
                .email("john@example.com")
                .password(passwordEncoder.encode("john123"))
                .roles(Set.of(User.Role.ROLE_USER))
                .enabled(true).build(),
            User.builder()
                .username("jane")
                .email("jane@example.com")
                .password(passwordEncoder.encode("jane123"))
                .roles(Set.of(User.Role.ROLE_USER))
                .enabled(true).build()
        ));
        log.info("Seeded users: admin / john / jane");
    }

    private void seedPlans() {
        if (planRepository.count() > 0) return;

        planRepository.saveAll(List.of(
            Plan.builder().name("Starter").provider("Verizon")
                .planType(Plan.PlanType.PREPAID).monthlyPrice(new BigDecimal("25.00"))
                .dataLimitGB(5).callMinutes(null).smsCount(null)
                .fiveGEnabled(false).internationalRoaming(false).hotspotEnabled(false)
                .contractMonths(0).setupFee(BigDecimal.ZERO)
                .description("Budget prepaid plan with basic data.")
                .additionalFeatures(List.of("Wi-Fi calling", "Mobile hotspot (1GB)"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Unlimited Plus").provider("Verizon")
                .planType(Plan.PlanType.POSTPAID).monthlyPrice(new BigDecimal("80.00"))
                .dataLimitGB(null).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(true).hotspotEnabled(true)
                .contractMonths(12).setupFee(new BigDecimal("30.00"))
                .description("Unlimited 5G plan with premium features.")
                .additionalFeatures(List.of("HD streaming", "50GB hotspot", "Travel pass included"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Connect Basic").provider("AT&T")
                .planType(Plan.PlanType.PREPAID).monthlyPrice(new BigDecimal("30.00"))
                .dataLimitGB(10).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(false).hotspotEnabled(true)
                .contractMonths(0).setupFee(BigDecimal.ZERO)
                .description("No-contract prepaid with 5G access.")
                .additionalFeatures(List.of("5G access", "Rollover data"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Unlimited Extra").provider("AT&T")
                .planType(Plan.PlanType.POSTPAID).monthlyPrice(new BigDecimal("75.00"))
                .dataLimitGB(null).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(true).hotspotEnabled(true)
                .contractMonths(24).setupFee(new BigDecimal("35.00"))
                .description("Full-featured unlimited plan for heavy users.")
                .additionalFeatures(List.of("HBO Max included", "75GB hotspot", "4K streaming"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Magenta").provider("T-Mobile")
                .planType(Plan.PlanType.POSTPAID).monthlyPrice(new BigDecimal("70.00"))
                .dataLimitGB(null).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(true).hotspotEnabled(true)
                .contractMonths(0).setupFee(BigDecimal.ZERO)
                .description("T-Mobile's flagship no-contract unlimited plan.")
                .additionalFeatures(List.of("Netflix Basic included", "International texting", "In-flight Wi-Fi"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Essentials").provider("T-Mobile")
                .planType(Plan.PlanType.POSTPAID).monthlyPrice(new BigDecimal("55.00"))
                .dataLimitGB(null).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(false).hotspotEnabled(false)
                .contractMonths(0).setupFee(BigDecimal.ZERO)
                .description("Entry unlimited postpaid with 5G.")
                .additionalFeatures(List.of("5G Ultra Capacity", "Scam Shield"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Family Share 4-Line").provider("Verizon")
                .planType(Plan.PlanType.FAMILY).monthlyPrice(new BigDecimal("160.00"))
                .dataLimitGB(null).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(true).hotspotEnabled(true)
                .contractMonths(24).setupFee(new BigDecimal("0.00"))
                .description("Unlimited family plan — 4 lines bundled together.")
                .additionalFeatures(List.of("Disney+ bundle", "Apple Music", "100GB hotspot per line"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Student Saver").provider("AT&T")
                .planType(Plan.PlanType.STUDENT).monthlyPrice(new BigDecimal("40.00"))
                .dataLimitGB(20).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(false).hotspotEnabled(true)
                .contractMonths(0).setupFee(BigDecimal.ZERO)
                .description("Discounted plan for verified students.")
                .additionalFeatures(List.of("Spotify Premium", "Student verification required", "Rollover data"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Business Pro").provider("T-Mobile")
                .planType(Plan.PlanType.BUSINESS).monthlyPrice(new BigDecimal("120.00"))
                .dataLimitGB(null).callMinutes(null).smsCount(null)
                .fiveGEnabled(true).internationalRoaming(true).hotspotEnabled(true)
                .contractMonths(24).setupFee(new BigDecimal("50.00"))
                .description("Enterprise-grade plan with priority data and SLA support.")
                .additionalFeatures(List.of("Priority network access", "Dedicated support", "200GB hotspot", "International calling to 30+ countries"))
                .status(Plan.PlanStatus.ACTIVE).build(),

            Plan.builder().name("Value 15GB").provider("Verizon")
                .planType(Plan.PlanType.PREPAID).monthlyPrice(new BigDecimal("45.00"))
                .dataLimitGB(15).callMinutes(null).smsCount(null)
                .fiveGEnabled(false).internationalRoaming(false).hotspotEnabled(true)
                .contractMonths(0).setupFee(BigDecimal.ZERO)
                .description("Great mid-tier prepaid with generous data.")
                .additionalFeatures(List.of("Carry-over data", "Wi-Fi calling"))
                .status(Plan.PlanStatus.ACTIVE).build()
        ));
        log.info("Seeded 10 sample telecom plans");
    }
}

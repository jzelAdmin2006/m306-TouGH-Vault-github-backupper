package com.jzel.toughvault.webservice.adapter.rest;

import static com.jzel.toughvault.common.config.Scheduling.MINUTES_SCAN_INTERVAL;
import static java.time.LocalDateTime.now;

import com.jzel.toughvault.business.service.RepoService;
import com.jzel.toughvault.integration.service.RegistrationService;
import com.jzel.toughvault.webservice.adapter.model.ScanInfoDto;
import com.jzel.toughvault.webservice.service.WebMapperService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GitHubController {

  private final RepoService repoService;
  private final WebMapperService webMapperService;
  private final ExecutorService manualScanExecutor;
  private final RegistrationService registrationService;
  private final AtomicReference<Optional<LocalDateTime>> lastManualScanTime =
      new AtomicReference<>(Optional.empty());

  @GetMapping("/scan")
  public ResponseEntity<ScanInfoDto> getScanInfo() {
    return ResponseEntity.ok(webMapperService.toDto(getLastScanTime(), getScanAllowedAt(), scanIsAllowed()));
  }

  @PutMapping("/scan")
  public ResponseEntity<Void> scanForGitHubChanges() {
    return scanIsAllowed() ? proceedScan() : ResponseEntity.status(429).build();
  }

  private LocalDateTime getLastScanTime() {
    final LocalDateTime now = now();
    LocalDateTime lastAutoScanTime = now
        .minusMinutes(now.getMinute() % MINUTES_SCAN_INTERVAL)
        .withSecond(0)
        .withNano(0);
    return registrationService.getInitFetchAt().filter(t -> t.isAfter(lastAutoScanTime))
        .orElse(lastManualScanTime.get().filter(t -> t.isAfter(lastAutoScanTime)).orElse(lastAutoScanTime));
  }

  private LocalDateTime getScanAllowedAt() {
    final LocalDateTime now = now();
    final LocalDateTime allowedEarliest = lastManualScanTime.get().map(t -> t.plusMinutes(MINUTES_SCAN_INTERVAL))
        .orElse(now);
    return allowedEarliest.isBefore(now) ? now : allowedEarliest;
  }

  private boolean scanIsAllowed() {
    return lastManualScanTime.get().map(t -> t.isBefore(now().minusMinutes(MINUTES_SCAN_INTERVAL))).orElse(true);
  }

  private ResponseEntity<Void> proceedScan() {
    lastManualScanTime.set(Optional.of(now()));
    manualScanExecutor.submit(repoService::scanForGitHubChanges);
    return ResponseEntity.accepted().build();
  }
}

package com.jzel.toughvault.webservice.adapter.rest;

import com.jzel.toughvault.business.service.SettingsService;
import com.jzel.toughvault.webservice.adapter.model.SettingsDto;
import com.jzel.toughvault.webservice.service.WebMapperService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
@AllArgsConstructor
public class SettingsController {

  private final SettingsService settingsService;
  private final WebMapperService webMapperService;

  @GetMapping()
  public ResponseEntity<SettingsDto> getSettings() {
    return ResponseEntity.ok(webMapperService.toDto(settingsService.getSettings()));
  }

  @PutMapping("/auto-repo-update")
  public ResponseEntity<SettingsDto> toggleAutoRepoUpdate() {
    return ResponseEntity.ok(webMapperService.toDto(settingsService.toggleAutoRepoUpdate()));
  }

  @PutMapping("/auto-commit-update")
  public ResponseEntity<SettingsDto> toggleAutoCommitUpdate() {
    return ResponseEntity.ok(webMapperService.toDto(settingsService.toggleAutoCommitUpdate()));
  }
}

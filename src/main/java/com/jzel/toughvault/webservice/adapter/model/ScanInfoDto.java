package com.jzel.toughvault.webservice.adapter.model;

import java.util.Date;

public record ScanInfoDto(Date lastScanTime, Date scanAllowedAt, boolean scanAllowed) {

}

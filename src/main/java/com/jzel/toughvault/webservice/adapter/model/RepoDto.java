package com.jzel.toughvault.webservice.adapter.model;

import java.util.Date;

public record RepoDto(int id, String name, String volumeLocation, boolean isPrivate, Date latestPush,
                      Date latestFetch) {

}

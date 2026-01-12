package net.springprojectbackend.springboot.service;

import java.util.List;

public interface BlockConfigParser {

	List<String> parseJsonArray(String jsonArrayOrNull);
}

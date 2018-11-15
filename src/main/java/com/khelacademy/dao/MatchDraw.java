package com.khelacademy.dao;

import java.util.List;
import java.util.Map;

import com.khelacademy.www.pojos.Fixtures;
import com.khelacademy.www.pojos.User;

public interface MatchDraw {
	Map<String, List<User>> groupPlayers(Integer eventId);
	Map<String, List<Fixtures>> makeFixture(Map<String, List<User>> groups);
}

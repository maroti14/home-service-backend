package com.homeservice.domain.slot.service;

import com.homeservice.common.enums.ServiceKey;
import com.homeservice.domain.slot.dto.request.BlockSlotRequest;
import com.homeservice.domain.slot.dto.request.UnblockSlotRequest;
import com.homeservice.domain.slot.dto.response.SlotGridResponse;
import com.homeservice.domain.slot.dto.response.SlotHeatmapResponse;

import java.time.LocalDate;
import java.util.List;

public interface SlotService {

	// get slot grid for a specific date
	SlotGridResponse getSlotGrid(Long cityId, ServiceKey serviceKey, LocalDate date);

	// get slot grids for multiple days
	List<SlotGridResponse> getSlotGridForDays(Long cityId, ServiceKey serviceKey, LocalDate fromDate, int days);

	// admin: block a slot
	void blockSlot(BlockSlotRequest req, String adminEmail);

	// admin: unblock a slot
	void unblockSlot(UnblockSlotRequest req);

	// admin: heatmap data
	SlotHeatmapResponse getHeatmap(Long cityId, LocalDate fromDate, LocalDate toDate);
}

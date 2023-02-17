package com.microsoft.gbb.reddog.makelineservice.repository;

import com.microsoft.gbb.reddog.makelineservice.dto.OrderSummaryDto;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.DaprPreviewClient;
import io.dapr.client.domain.QueryStateRequest;
import io.dapr.client.domain.QueryStateResponse;
import io.dapr.client.domain.query.Query;
import io.dapr.client.domain.query.filters.AndFilter;
import io.dapr.client.domain.query.filters.EqFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("orderSummaryRepository")
public class OrderSummaryRepositoryImpl implements OrderSummaryRepository {
  private final DaprClient client = (new DaprClientBuilder()).build();
  private final DaprPreviewClient previewClient = (new DaprClientBuilder()).buildPreviewClient();
  private final String stateStoreName = "reddog.statestore.orders";

  @Override
  public OrderSummaryDto saveOrder(OrderSummaryDto orderSummary) {
    client.saveState(stateStoreName, orderSummary.getOrderId(), orderSummary).block();
    return orderSummary;
  }

  @Override
  public ArrayList<OrderSummaryDto> getOrdersForStore(String storeId) {
    return new ArrayList<>(findAllByStoreId(storeId));
  }

  @Override
  public List<OrderSummaryDto> findAllByStoreId(String storeId) {
    return findByParam("storeId", storeId);
  }

  @Override
  public OrderSummaryDto findByOrderIdAndStoreId(String orderId, String storeId) {
    Query query = new Query()
        .setFilter(new AndFilter()
            .addClause(new EqFilter<>("orderId", orderId))
            .addClause(new EqFilter<>("storeId", storeId)));
    QueryStateRequest request = new QueryStateRequest(stateStoreName)
        .setQuery(query);

    QueryStateResponse<OrderSummaryDto> result = previewClient.queryState(request, OrderSummaryDto.class).block();
    return result.getResults().get(0).getValue();
  }

  @Override
  public List<OrderSummaryDto> findByOrderId(String orderId) {
    return findByParam("orderId", orderId);
  }

  private List<OrderSummaryDto> findByParam(String param, String id) {
    Query query = new Query()
        .setFilter(new EqFilter<>(param, id));
    QueryStateRequest request = new QueryStateRequest(stateStoreName)
        .setQuery(query);

    QueryStateResponse<OrderSummaryDto> result = previewClient.queryState(request, OrderSummaryDto.class).block();
    return result.getResults().stream().map(r -> r.getValue()).toList();
  }

}

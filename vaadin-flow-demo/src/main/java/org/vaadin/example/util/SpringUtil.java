package org.vaadin.example.util;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class SpringUtil implements Serializable {
    /**
     * A method to convert given Vaadin sort hints to Spring Data specific sort
     * instructions.
     *
     * @param vaadinSortOrders a list of Vaadin QuerySortOrders to convert to
     * @return the Sort object for Spring Data repositories
     */
    public static Sort convertSortOrders(List<QuerySortOrder> vaadinSortOrders) {
        return Sort.by(
                vaadinSortOrders.stream()
                        .map(sortOrder ->
                                sortOrder.getDirection() == SortDirection.ASCENDING ?
                                        Sort.Order.asc(sortOrder.getSorted()) :
                                        Sort.Order.desc(sortOrder.getSorted())
                        )
                        .collect(Collectors.toList())
        );
    }
}
package com.synet.net.route;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class RouteDefinition {

    @NotEmpty
    private String id = UUID.randomUUID().toString();

    @NotNull
    private String uri;

    private int order;

    @NotNull
    private List<PredicateDefinition> predicates;

}

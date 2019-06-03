package com.synet.net.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.Ordered;

import java.util.List;

@Data
@AllArgsConstructor
public class Route implements Ordered {

    private final String id;

    private final String uri;

    private final int order;

    private final List<PredicateDefinition> predicates;
}

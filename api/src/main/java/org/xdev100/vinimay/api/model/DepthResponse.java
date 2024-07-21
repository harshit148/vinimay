package org.xdev100.vinimay.api.model;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepthResponse extends MessageFromOrderBook {
    private String market;
    private  List<String[]> bids;
    private List<String[]> asks;
}

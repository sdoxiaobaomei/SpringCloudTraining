package org.chai.resolver.entity.vo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderResultVO {
    private String name;
    private String size;
    private String date;

    public static OrderResultVO build(File file) {
        OrderResultVO orderResultVO = new OrderResultVO();
        orderResultVO.setName(file.getName());
        orderResultVO.setSize((double)file.length()/1024 + " kb");
        orderResultVO.setDate(new SimpleDateFormat("yyyy年MM月dd日 HH时mm分")
                .format(new Date(file.lastModified())));
        return orderResultVO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

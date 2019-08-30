package com.jaclon.bootsecurity.page.table;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询处理器
 * @author jaclon
 * @date 2019/8/26
 */
public class PageTableHandler {

    private CountHandler countHandler;
    private ListHandler listHandler;
    private OrderHandler orderHandler;

    public PageTableHandler(CountHandler countHandler, ListHandler listHandler) {
        super();
        this.countHandler = countHandler;
        this.listHandler = listHandler;
    }

    public PageTableHandler(CountHandler countHandler, ListHandler listHandler, OrderHandler orderHandler) {
        this(countHandler, listHandler);
        this.orderHandler = orderHandler;
    }

    public PageTableResponse handle(PageTableRequest ptRequest){
        int count = 0 ;
        List<?> list = null;

        count = this.countHandler.count(ptRequest);
        if(count > 0){
            if(orderHandler != null){
                ptRequest = orderHandler.order(ptRequest);
            }
            list = this.listHandler.list(ptRequest);
        }

        if(list == null){
            list = new ArrayList <>();
        }
        return new PageTableResponse(count, count,list);

    }

    public interface ListHandler {
        List<?> list(PageTableRequest request);
    }

    public interface CountHandler {
        int count(PageTableRequest request);
    }

    public interface OrderHandler {
        PageTableRequest order(PageTableRequest request);
    }
}

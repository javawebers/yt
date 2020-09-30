package com.github.yt.commons.query;

import java.util.List;

public interface IPage<T> {

    int getPageNo();

    int getPageSize();

    int getTotalCount();

    List<T> getData();

}

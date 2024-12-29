package com.projectmap2.Repository;

import com.projectmap2.Domain.Entity;
import com.projectmap2.Utils.Paging.Page;
import com.projectmap2.Utils.Paging.Pageable;

public interface PagedRepository<ID,E extends Entity<ID>> extends Repository<ID,E>{
    Page<E> findAllOnPage(Pageable pageable);

}

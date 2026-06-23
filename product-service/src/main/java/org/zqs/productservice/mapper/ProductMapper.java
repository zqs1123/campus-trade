package org.zqs.productservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.zqs.productservice.entity.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
}
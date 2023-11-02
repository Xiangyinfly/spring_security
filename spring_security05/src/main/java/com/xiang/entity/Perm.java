package com.xiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_perm")
public class Perm {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("tag")
    private String tag;

    @TableField("user_id")
    private Long user_id;

    @TableField(exist = false)
    private User user;
}

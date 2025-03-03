package com.hmall.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_permission")
public class Permission {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String path;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
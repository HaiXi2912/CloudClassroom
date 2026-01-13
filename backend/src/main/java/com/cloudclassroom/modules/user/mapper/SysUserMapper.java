package com.cloudclassroom.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloudclassroom.modules.user.dto.AdminUserRow;
import com.cloudclassroom.modules.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表 Mapper。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

	@Select("""
			<script>
			SELECT
				u.id,
				u.username,
				u.nickname,
				u.status,
				u.created_at AS createdAt,
				u.updated_at AS updatedAt,
				r.role_code AS roleCode,
				r.role_name AS roleName
			FROM sys_user u
			LEFT JOIN sys_user_role ur ON ur.user_id = u.id AND ur.deleted = 0
			LEFT JOIN sys_role r ON r.id = ur.role_id AND r.deleted = 0
			WHERE u.deleted = 0
			<if test='username != null and username.trim() != ""'>
				AND u.username LIKE CONCAT('%', #{username}, '%')
			</if>
			<if test='status != null'>
				AND u.status = #{status}
			</if>
			<if test='roleCode != null and roleCode.trim() != ""'>
				AND r.role_code = #{roleCode}
			</if>
			ORDER BY u.id DESC
			</script>
			""")
	IPage<AdminUserRow> selectAdminUserPage(IPage<AdminUserRow> page,
																				 @Param("username") String username,
																				 @Param("status") Integer status,
																				 @Param("roleCode") String roleCode);

	@Select("""
			SELECT
				u.id,
				u.username,
				u.nickname,
				u.status,
				u.created_at AS createdAt,
				u.updated_at AS updatedAt,
				r.role_code AS roleCode,
				r.role_name AS roleName
			FROM sys_user u
			LEFT JOIN sys_user_role ur ON ur.user_id = u.id AND ur.deleted = 0
			LEFT JOIN sys_role r ON r.id = ur.role_id AND r.deleted = 0
			WHERE u.deleted = 0
				AND u.id = #{id}
			LIMIT 1
			""")
	AdminUserRow selectAdminUserRowById(@Param("id") Long id);
}

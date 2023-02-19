
-- tables
-- users
CREATE TABLE `users` (`id` binary(16) NOT NULL DEFAULT (uuid_to_bin(uuid(),true)) COMMENT '사용자의 GUID',
                      `first_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이름',
                      `last_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '성',
                      `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이메일',
                      `picture` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '사진',
                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      `deleted_at` datetime DEFAULT NULL,
                      PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

-- posts
CREATE TABLE `posts` (`id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '기본 키',
                      `user_id` binary(16) NOT NULL COMMENT '작성자. users 테이블의 레코드 ID',
                      `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '게시글 본문',
                      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
                      `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정시점',
                      `deleted_at` datetime DEFAULT NULL COMMENT '삭제시점',
                      `read_count` int NOT NULL DEFAULT '0' COMMENT '게시글 조회수',
                      `like_count` int NOT NULL DEFAULT '0' COMMENT '게시글 좋아요 수',
                      PRIMARY KEY (`id`),
                      CONSTRAINT `user_id_post_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)) ENGINE=InnoDB AUTO_INCREMENT=61229 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

-- comments
CREATE TABLE `post_comments` (`id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '기본 키',
                              `post_id` int unsigned NOT NULL,
                              `user_id` binary(16) NOT NULL COMMENT '작성자. users 테이블의 레코드 ID',
                              `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '코멘트 본문',
                              `parent_id` int unsigned DEFAULT NULL,
                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시점',
                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종수정시점',
                              `deleted_at` datetime DEFAULT NULL COMMENT '삭제시점',
                              `like_count` int NOT NULL DEFAULT '0' COMMENT '댓글 좋아요 수',
                              PRIMARY KEY (`id`),
                              KEY `post_id` (`post_id`),
                              KEY `parent_id_IDX` (`parent_id`) USING BTREE,
                              CONSTRAINT `post_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)) ENGINE=InnoDB AUTO_INCREMENT=129477 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
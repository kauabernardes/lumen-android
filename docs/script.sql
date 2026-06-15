
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "user" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "email" varchar(255) NOT NULL,
    "username" varchar(50) NOT NULL,
    "password" varchar(255) NOT NULL,
    "profileImage" varchar(255),
    "resetPasswordToken" varchar(255),
    "resetPasswordExpires" timestamptz,
    CONSTRAINT "PK_user" PRIMARY KEY ("id"),
    CONSTRAINT "UQ_user_email" UNIQUE ("email"),
    CONSTRAINT "UQ_user_username" UNIQUE ("username")
);

CREATE TABLE "reward" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "title" varchar(255) NOT NULL,
    "difficulty" varchar(32) NOT NULL,
    "createdAt" timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT "PK_reward" PRIMARY KEY ("id")
);

CREATE TABLE "community" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "name" varchar(50) NOT NULL,
    "description" varchar(255) NOT NULL,
    "authorId" uuid NOT NULL,
    "createdAt" timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT "PK_community" PRIMARY KEY ("id"),
    CONSTRAINT "UQ_community_name" UNIQUE ("name"),
    CONSTRAINT "FK_community_author" FOREIGN KEY ("authorId") REFERENCES "user"("id") ON DELETE NO ACTION
);
CREATE INDEX "IDX_community_authorId" ON "community" ("authorId");

CREATE TABLE "session" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "hostId" uuid NOT NULL,
    "createdAt" timestamptz DEFAULT now() NOT NULL,
    "finishedAt" timestamptz,
    CONSTRAINT "PK_session" PRIMARY KEY ("id"),
    CONSTRAINT "FK_session_host" FOREIGN KEY ("hostId") REFERENCES "user"("id") ON DELETE CASCADE
);
CREATE INDEX "IDX_session_hostId" ON "session" ("hostId");

CREATE TABLE "agenda_events" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "title" varchar NOT NULL,
    "description" text,
    "eventDate" timestamp NOT NULL,
    "userId" uuid,
    "createdAt" timestamp DEFAULT now() NOT NULL,
    CONSTRAINT "PK_agenda_events" PRIMARY KEY ("id"),
    CONSTRAINT "FK_agenda_events_user" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE
);

CREATE TABLE "daily_logs" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "userId" uuid NOT NULL,
    "mood" varchar NOT NULL,
    "studiedYesterday" text NOT NULL,
    "achievedGoal" varchar NOT NULL,
    "studyToday" text NOT NULL,
    "createdAt" timestamp DEFAULT now() NOT NULL,
    CONSTRAINT "PK_daily_logs" PRIMARY KEY ("id"),
    CONSTRAINT "FK_daily_logs_user" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE
);

CREATE TABLE "user_reward" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "isCorrect" boolean NOT NULL,
    "userId" uuid,
    "rewardId" uuid,
    CONSTRAINT "PK_user_reward" PRIMARY KEY ("id"),
    CONSTRAINT "FK_user_reward_user" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE NO ACTION,
    CONSTRAINT "FK_user_reward_reward" FOREIGN KEY ("rewardId") REFERENCES "reward"("id") ON DELETE NO ACTION
);

CREATE TABLE "participant_session" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "sessionId" uuid NOT NULL,
    "userId" uuid NOT NULL,
    "createdAt" timestamptz DEFAULT now() NOT NULL,
    "time" bigint DEFAULT 0 NOT NULL,
    CONSTRAINT "PK_participant_session" PRIMARY KEY ("id"),
    CONSTRAINT "FK_participant_session_session" FOREIGN KEY ("sessionId") REFERENCES "session"("id") ON DELETE CASCADE,
    CONSTRAINT "FK_participant_session_user" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE
);
CREATE INDEX "IDX_participant_session_sessionId" ON "participant_session" ("sessionId");
CREATE INDEX "IDX_participant_session_userId" ON "participant_session" ("userId");

CREATE TABLE "member" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "userId" uuid NOT NULL,
    "communityId" uuid NOT NULL,
    CONSTRAINT "PK_member" PRIMARY KEY ("id"),
    CONSTRAINT "UQ_member_userId_communityId" UNIQUE ("userId", "communityId"),
    CONSTRAINT "FK_member_user" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE,
    CONSTRAINT "FK_member_community" FOREIGN KEY ("communityId") REFERENCES "community"("id") ON DELETE CASCADE
);
CREATE INDEX "IDX_member_userId" ON "member" ("userId");
CREATE INDEX "IDX_member_communityId" ON "member" ("communityId");

CREATE TABLE "post" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "content" varchar(255) NOT NULL,
    "userId" uuid NOT NULL,
    "communityId" uuid NOT NULL,
    "createdAt" timestamptz DEFAULT now() NOT NULL,
    "parentId" uuid,
    CONSTRAINT "PK_post" PRIMARY KEY ("id"),
    CONSTRAINT "FK_post_parent" FOREIGN KEY ("parentId") REFERENCES "post"("id") ON DELETE CASCADE,
    CONSTRAINT "FK_post_user" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE,
    CONSTRAINT "FK_post_community" FOREIGN KEY ("communityId") REFERENCES "community"("id") ON DELETE CASCADE
);
CREATE INDEX "IDX_post_userId" ON "post" ("userId");
CREATE INDEX "IDX_post_communityId" ON "post" ("communityId");

CREATE TABLE "like" (
    "id" uuid DEFAULT uuid_generate_v4() NOT NULL,
    "userId" uuid NOT NULL,
    "postId" uuid NOT NULL,
    CONSTRAINT "PK_like" PRIMARY KEY ("id"),
    CONSTRAINT "UQ_like_userId_postId" UNIQUE ("userId", "postId"),
    CONSTRAINT "FK_like_post" FOREIGN KEY ("postId") REFERENCES "post"("id") ON DELETE NO ACTION
);
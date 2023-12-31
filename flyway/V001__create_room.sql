-- Table: public.room

-- DROP TABLE IF EXISTS public.room;

CREATE TABLE IF NOT EXISTS public.room
(
    id bigint NOT NULL,
    name character varying(128) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT room_pkey PRIMARY KEY (id),
    CONSTRAINT room_unique_name UNIQUE (name)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.room
    OWNER to jboss;


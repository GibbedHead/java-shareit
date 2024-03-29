DROP TABLE IF EXISTS public.comments CASCADE ;
DROP TABLE IF EXISTS public.bookings CASCADE ;
DROP TABLE IF EXISTS public.items CASCADE ;
DROP TABLE IF EXISTS public.requests CASCADE ;
DROP TABLE IF EXISTS public.users CASCADE ;

CREATE TABLE public.users (
	id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	name varchar NOT NULL,
	email varchar NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT users_un UNIQUE (email)
);

CREATE TABLE public.requests (
	id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	description varchar NOT NULL,
	requestor_id bigint NOT NULL,
	created timestamp without time zone NOT NULL,
	CONSTRAINT requests_pk PRIMARY KEY (id),
	CONSTRAINT requests_users_fk FOREIGN KEY (requestor_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TABLE public.items (
	id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	name varchar NOT NULL,
	description varchar NOT NULL,
	is_available boolean NOT NULL,
	owner_id bigint NOT NULL,
	request_id bigint NULL,
	CONSTRAINT items_pk PRIMARY KEY (id),
	CONSTRAINT items_users_fk FOREIGN KEY (owner_id) REFERENCES public.users(id) ON DELETE CASCADE,
	CONSTRAINT items_request_fk FOREIGN KEY (request_id) REFERENCES public.requests(id) ON DELETE SET NULL
);

CREATE TABLE public.bookings (
	id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	start_date timestamp without time zone NOT NULL,
	end_date timestamp without time zone NOT NULL,
	item_id bigint NOT NULL,
	booker_id bigint NOT NULL,
	status varchar NOT NULL,
	CONSTRAINT bookings_pk PRIMARY KEY (id),
	CONSTRAINT bookings_items_fk FOREIGN KEY (item_id) REFERENCES public.items(id) ON DELETE CASCADE,
	CONSTRAINT bookings_users_fk FOREIGN KEY (booker_id) REFERENCES public.users(id) ON DELETE CASCADE
);


CREATE TABLE public.comments (
	id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	text varchar NOT NULL,
	item_id bigint NOT NULL,
	author_id bigint NOT NULL,
	created timestamp without time zone NOT NULL,
	CONSTRAINT comments_pk PRIMARY KEY (id),
	CONSTRAINT comments_items_fk FOREIGN KEY (item_id) REFERENCES public.items(id) ON DELETE CASCADE,
	CONSTRAINT comments_users_fk FOREIGN KEY (author_id) REFERENCES public.users(id) ON DELETE CASCADE
);

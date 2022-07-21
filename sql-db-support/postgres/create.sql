
CREATE TABLE public.json_test (
	id varchar NOT NULL,
	json_data jsonb NULL,
	CONSTRAINT jsontest_pk PRIMARY KEY (id)
);

create view my_view as  select * from json_test

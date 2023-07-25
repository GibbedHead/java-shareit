INSERT INTO public.users (id,name,email) VALUES
	 (1,'updateName','updateName@user.com'),
	 (4,'user','user@user.com'),
	 (5,'other','other@other.com'),
	 (6,'practicum','practicum@yandex.ru');

INSERT INTO public.requests (id,description,requestor_id,created) VALUES
	 (1,'Хотел бы воспользоваться щёткой для обуви',1,'2023-06-27 22:34:41');

INSERT INTO public.items (id,name,description,is_available,owner_id,request_id) VALUES
	 (1,'Аккумуляторная дрель','Аккумуляторная дрель + аккумулятор',true,1,NULL),
	 (3,'Клей Момент','Тюбик суперклея марки Момент',true,4,NULL),
	 (2,'Отвертка','Аккумуляторная отвертка',true,4,NULL),
	 (4,'Кухонный стол','Стол для празднования',true,6,NULL),
	 (5,'Щётка для обуви','Стандартная щётка для обуви',true,4,1);

INSERT INTO public.bookings (id,start_date,end_date,item_id,booker_id,status) VALUES
	 (1,'2023-06-27 22:34:34','2023-06-27 22:34:35',2,1,'APPROVED'),
	 (2,'2023-06-28 22:34:31','2023-06-29 22:34:31',2,1,'APPROVED'),
	 (3,'2023-06-28 22:34:33','2023-06-28 23:34:33',1,4,'REJECTED'),
	 (4,'2023-06-27 23:34:33','2023-06-28 00:34:33',2,5,'APPROVED'),
	 (5,'2023-06-27 22:34:41','2023-06-28 22:34:38',3,1,'REJECTED'),
	 (6,'2023-06-27 22:34:41','2023-06-27 22:34:42',2,1,'APPROVED'),
	 (8,'2023-06-27 22:34:57','2023-06-27 23:34:55',4,1,'APPROVED'),
	 (7,'2023-07-07 22:34:55','2023-07-08 22:34:55',1,5,'APPROVED');

INSERT INTO public.comments (id,text,item_id,author_id,created) VALUES
	 (1,'Add comment from user1',2,1,'2023-06-27 22:35:02.142866');

INSERT INTO public.users (id,"name",email) VALUES
	 (1,'updateName','updateName@user.com'),
	 (4,'user','user@user.com'),
	 (5,'other','other@other.com'),
	 (6,'practicum','practicum@yandex.ru');

INSERT INTO public.items (id,"name",description,is_available,owner_id,request_id) VALUES
	 (1,'Аккумуляторная дрель','Аккумуляторная дрель + аккумулятор',true,1,NULL),
	 (3,'Клей Момент','Тюбик суперклея марки Момент',true,4,NULL),
	 (2,'Отвертка','Аккумуляторная отвертка',true,4,NULL),
	 (4,'Кухонный стол','Стол для празднования',true,6,NULL);

INSERT INTO public.bookings (id,start_date,end_date,item_id,booker_id,status) VALUES
	 (1,'2023-06-19 11:47:47','2023-06-19 11:47:48',2,1,'APPROVED'),
	 (2,'2023-06-20 11:47:44','2023-06-21 11:47:44',2,1,'APPROVED'),
	 (3,'2023-06-20 11:47:46','2023-06-20 12:47:46',1,4,'REJECTED'),
	 (4,'2023-06-19 12:47:46','2023-06-19 13:47:46',2,5,'APPROVED'),
	 (5,'2023-06-19 11:47:53','2023-06-20 11:47:50',3,1,'REJECTED'),
	 (6,'2023-06-19 11:47:54','2023-06-19 11:47:55',2,1,'APPROVED'),
	 (8,'2023-06-19 11:48:08','2023-06-19 12:48:06',4,1,'APPROVED'),
	 (7,'2023-06-29 11:48:00','2023-06-30 11:48:00',1,5,'APPROVED');

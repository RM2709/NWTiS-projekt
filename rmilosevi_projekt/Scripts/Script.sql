SELECT * FROM PUBLIC.LETOVI_POLASCI ;

DELETE FROM LETOVI_POLASCI WHERE CAST(TIMESTAMP(FIRSTSEEN) AS DATE)='2022-12-01'


SELECT * FROM AERODROMI_LETOVI;

SELECT COUNT(DISTINCT (ESTDEPARTUREAIRPORT, FIRSTSEEN)) AS BROJ_PREUZETIH_POLAZAKA FROM LETOVI_POLASCI;

SELECT ESTDEPARTUREAIRPORT, CAST(TIMESTAMP(FIRSTSEEN) AS DATE) AS DATUM, COUNT(*) AS BROJ_POLAZAKA FROM LETOVI_POLASCI GROUP BY ESTDEPARTUREAIRPORT, DATUM ORDER BY ESTDEPARTUREAIRPORT, DATUM;

SELECT CAST(TIMESTAMP(FIRSTSEEN) AS DATE) AS DATUM, COUNT(*) AS BROJ_POLAZAKA FROM LETOVI_POLASCI GROUP BY DATUM ORDER BY DATUM;

SELECT ESTDEPARTUREAIRPORT, CAST(TIMESTAMP(FIRSTSEEN) AS DATE) AS DATUM, COUNT(*) AS BROJ_POLAZAKA FROM LETOVI_POLASCI WHERE ESTDEPARTUREAIRPORT='LDZA' GROUP BY ESTDEPARTUREAIRPORT, DATUM ORDER BY ESTDEPARTUREAIRPORT, DATUM;

SELECT ESTDEPARTUREAIRPORT, CAST(TIMESTAMP(FIRSTSEEN) AS DATE) AS DATUM, COUNT(*) AS BROJ_POLAZAKA FROM LETOVI_POLASCI WHERE CAST(TIMESTAMP(FIRSTSEEN) AS DATE)='2020-11-11' GROUP BY ESTDEPARTUREAIRPORT, DATUM ORDER BY ESTDEPARTUREAIRPORT, DATUM;
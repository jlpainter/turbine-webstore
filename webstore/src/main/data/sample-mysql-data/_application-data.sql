
##
## application-data.sql
##
## Torque will not autogenerate these files anymore - please run
## this SQL code maually to get your application up and running
##

INSERT INTO COMPANY_CONFIG 
	(REF_ID, COMPANY_NAME, ADMIN_EMAIL, PHONE, FAX, PAYPAL_CLIENT_ID, PAYPAL_CLIENT_SECRET, PAYPAL_SANDBOX, TAX_RATE,
	ADDRESS_LINE_1 ,ADDRESS_LINE_2 ,CITY ,STATE ,ZIP ,COUNTRY, QUOTE_DAYS, CONFIRMATION_TERMS, QUOTE_TERMS, PO_TERMS, INVOICE_TERMS )
    VALUES (1,'MyWebstore', 'orders@mystore.abc', '800-555-1234', '', 
    '', '', 1, 0.00,
    '123 Main St.','Suite 100','AnyCity','ST','12345','US', 30, 
    'This Order Confirmation lists all details and costs except freight and insurance related to your order (unless stated differently in the notes section above). Please check it carefully and report any changes or mistakes to us immediately. We will not be responsible if there are any discrepancies between your Purchase Order and our Order Confirmation if they have not been reported within 4 days from the date of Confirmation. As it is not always possible to imprint the exact quantity ordered, it is agreed that an overrun or under-run of not more than 10% to be billed pro rata, is acceptable as fulfillment of this order.', 
    'This Quote lists all details and costs except freight and insurance related to your order (unless stated differently in the notes section above). Please check it carefully and report any changes or mistakes to us immediately. We will not be responsible if there are any discrepancies between your Purchase Order and our Order Confirmation if they have not been reported within 4 days from the date of Confirmation.', 
    'This Purchase Order lists all details except freight and insurance related to this order (unless stated differently in the special instructions section above). Please check it carefully and report any changes or mistakes to us immediately.', 
    'Past due accounts subject to service charge equal to 1.5% per month (annual rate 18%). There will be a late charge of $20.00 added to your account if it is turned over for collection. Title to purchase reflected upon this invoice shall remain to the benefit of the seller and shall not pass until payment in full has been received. In the event of default in payment, purchaser agrees to pay attorney fees, filing fees, reasonable collection costs and late charges as dictated by the state law.' );
    
    
INSERT INTO HOME_PAGE (REF_ID, WELCOME_MESSAGE, FILE_NAME ) VALUES ( 1, '<h2>  Enter Your Headline Here </h2><p>Describe your company here</p>', 'homepage_image.jpg');
INSERT INTO ITEM_CATEGORY (REF_ID, CATEGORY_NAME) values (1, 'Apparel');
INSERT INTO ITEM_CATEGORY (REF_ID, CATEGORY_NAME) values (2, 'Homegoods');
INSERT INTO ITEM_CATEGORY (REF_ID, CATEGORY_NAME) values (3, 'Electronics');

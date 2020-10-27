
        package ke.co.mobiticket.mobiticket.retrofit.requests;

        public class ServerRegisterRequest {
        private String action;
        private String first_name;
        private String middle_name;
        private String last_name;
        private String date_of_birth;
        private String id_number;
        private String phone_number;
        private String email_address;
        private String password;
        private String status="Active";
        private String street_address="";
        private String city="";
        private String country="Kenya";

        public void setStreet_address(String street_address) {
        this.street_address = street_address;
        }

        public void setCity(String city) {
        this.city = city;
        }

        public void setCountry(String country) {
        this.country = country;
        }

        public void setAction(String action) {
        this.action = action;
        }

        public void setFirst_name(String first_name) {
        this.first_name = first_name;
        }

        public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
        }

        public void setLast_name(String last_name) {
        this.last_name = last_name;
        }

        public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
        }

        public void setId_number(String id_number) {
        this.id_number = id_number;
        }

        public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
        }

        public void setEmail_address(String email_address) {
        this.email_address = email_address;
        }

        public void setPassword(String password) {
        this.password = password;
        }
        }



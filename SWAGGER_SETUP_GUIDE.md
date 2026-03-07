# 🚀 SWAGGER UI - HƯỚNG DẪN SỬ DỤNG

## ✅ ĐÃ ĐƯỢC SETUP

Project đã có:
- ✅ SpringDoc OpenAPI dependency (pom.xml)
- ✅ OpenApiConfig.java - Config Swagger UI
- ✅ application.yaml - Enable Swagger UI
- ✅ SecurityConfig - Allow access to Swagger
- ✅ @Tag, @Operation annotations trên controllers

---

## 🌐 CÁCH TRUY CẬP SWAGGER UI

### **URL Swagger:**
```
http://localhost:8080/swagger-ui.html
```

### **API Docs JSON:**
```
http://localhost:8080/v3/api-docs
```

---

## 🚀 BƯỚC CHẠY

### **1. Rebuild Project**
```bash
cd /Applications/Workspace/DATN/DO_AN_TOT_NGHIEP/ExamInvigilationManagement
./mvnw clean package -DskipTests=true
```

### **2. Chạy Spring Boot**
```bash
java -jar target/ExamInvigilationManagement-0.0.1-SNAPSHOT.jar
```

Hoặc dùng IDE: Run > Run 'ExamInvigilationManagementApplication'

### **3. Mở Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 AUTHORIZE (Thêm JWT Token)

### **Cách thêm Authorization Token:**

1. **Trang Swagger UI** → Tìm button **"Authorize"** (góc trên phải)
2. **Nhấp vào "Authorize"**
3. **Paste JWT token vào field:**
   ```
   Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMjRhYmU5M...
   ```
4. **Nhấp "Authorize"** → Sẽ được sử dụng trong tất cả requests

---

## 🧪 TEST ENDPOINTS

### **1. Test Tạo User (POST /users)**

**Request:**
```json
{
  "username": "lecturer_test",
  "password": "pass123",
  "firstName": "Test",
  "lastName": "Lecturer",
  "email": "lecturer@hau.edu.vn",
  "roleIds": [3],
  "departmentId": 1,
  "academicTitle": "Th.S",
  "specialization": "Toán"
}
```

**Expected Response:**
```json
{
  "id": "uuid-xxx",
  "username": "lecturer_test",
  "firstName": "Test",
  "lastName": "Lecturer",
  "email": "lecturer@hau.edu.vn",
  "roles": [...]
}
```

### **2. Test Lấy Danh Sách Users (GET /users/paginated)**

**Params:**
- `page`: 0
- `size`: 10
- `sortBy`: id
- `sortDirection`: ASC

**Expected:** Page của 10 users

### **3. Test Tạo Lecturer (POST /api/lecturers)**

❌ **ENDPOINT NÀY ĐÃ BỊ XÓA**

Thay vào đó: Tạo User với role LECTURER → Lecturer sẽ tự động tạo

---

## 📝 ENDPOINTS CỦA PROJECT

### **Authentication:**
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/change-password` - Đổi password

### **Users:**
- `POST /users` - Tạo user
- `GET /users` - Lấy tất cả users
- `GET /users/{id}` - Lấy user theo ID
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Xóa user
- `GET /users/paginated` - Lấy users có phân trang
- `PUT /users/{userId}/assign-role/{roleId}` - Gán role

### **Lecturers:**
- `GET /api/lecturers` - Lấy danh sách giảng viên
- `GET /api/lecturers/{id}` - Lấy giảng viên theo ID
- `PUT /api/lecturers/{id}` - Update giảng viên
- `DELETE /api/lecturers/{id}` - Xóa giảng viên
- `GET /api/lecturers/paginated` - Lấy giảng viên có phân trang
- `GET /api/lecturers/search` - Tìm kiếm giảng viên

### **Roles:**
- `GET /api/roles` - Lấy danh sách roles
- `POST /api/roles` - Tạo role (ADMIN only)
- `PUT /api/roles/{id}` - Update role (ADMIN only)
- `DELETE /api/roles/{id}` - Xóa role (ADMIN only)

### **Departments:**
- `GET /api/departments` - Lấy danh sách bộ phận
- `POST /api/departments` - Tạo bộ phật (ADMIN only)
- `PUT /api/departments/{id}` - Update bộ phậ (ADMIN only)
- `DELETE /api/departments/{id}` - Xóa bộ phận (ADMIN only)

### **Exam Schedules:**
- `GET /api/exam-schedules` - Lấy danh sách lịch thi
- `POST /api/exam-schedules` - Tạo lịch thi (ADMIN only)
- `PUT /api/exam-schedules/{id}` - Update lịch thi (ADMIN only)
- `DELETE /api/exam-schedules/{id}` - Xóa lịch thi (ADMIN only)

### **Assignments:**
- `POST /api/exam-schedules/{id}/assign-written` - Phân công giảng viên

### **Payments:**
- `GET /api/payments` - Lấy danh sách thanh toán
- `POST /api/payments` - Tạo thanh toán (ADMIN/ACCOUNTING only)

---

## 🎯 TEST FLOW CHỦ ĐỀ LECTURER

### **Test Tạo Lecturer tự động:**

1. **POST /users** - Tạo user với role LECTURER
   ```json
   {
     "username": "lecturer_final_test",
     "password": "pass123",
     "firstName": "Final",
     "lastName": "Test",
     "email": "final@hau.edu.vn",
     "roleIds": [3],
     "departmentId": 1,
     "academicTitle": "TS",
     "specialization": "Lập trình"
   }
   ```
   ✅ Response: User được tạo thành công

2. **Check Backend Logs:**
   ```
   ✅ [UserService] User: lecturer_final_test
   ✅ [UserService] Roles: [ROLE_LECTURER]
   ✅ [UserService] Has LECTURER role: true
   ✅ [UserService] DepartmentId: 1
   ✅ [UserService] Creating Lecturer...
   ✅ [UserService] Lecturer created successfully!
   ```

3. **GET /api/lecturers** - Xem danh sách giảng viên
   ✅ Sẽ thấy giảng viên mới được tạo

4. **Query Database:**
   ```sql
   SELECT * FROM lecturers WHERE user_id = 'uuid-xxx';
   ```
   ✅ Sẽ có 1 record với:
   - fullName: Final Test
   - academicTitle: TS
   - specialization: Lập trình
   - departmentId: 1

---

## 🔍 TIPS TỪ SWAGGER UI

1. **Try it out** - Nhấp button này để test endpoint
2. **Execute** - Nhấp để gửi request
3. **Response** - Xem kết quả trả về
4. **Response headers** - Xem status code, content-type, etc.
5. **Copy as cURL** - Copy request để dùng trong Postman

---

## ❌ TROUBLESHOOT

### **Swagger UI không load:**
- ✅ Check port: `http://localhost:8080`
- ✅ Check logs: Xem có lỗi gì không
- ✅ Check browser console: F12 → Console tab

### **Authorize không hoạt động:**
- ✅ Token phải có tiền tố `Bearer `
- ✅ Token phải hợp lệ (chưa hết hạn)
- ✅ Check: Đã login và lấy token chưa?

### **Endpoint return 403:**
- ✅ Check role của user hiện tại
- ✅ Check @PreAuthorize annotation
- ✅ Check SecurityConfig rules

### **Endpoint return 404:**
- ✅ Check base path: `/api/...` hay `/users`
- ✅ Check typo trong endpoint path
- ✅ Check server đã start không

---

## 📚 THAM KHẢO

- **Swagger/OpenAPI:** https://swagger.io
- **SpringDoc:** https://springdoc.org
- **OpenAPI 3.0 Spec:** https://spec.openapis.org/oas/v3.0.3


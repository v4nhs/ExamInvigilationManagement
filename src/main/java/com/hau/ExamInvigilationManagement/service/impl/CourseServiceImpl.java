package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.CourseRequest;
import com.hau.ExamInvigilationManagement.dto.response.CourseResponse;
import com.hau.ExamInvigilationManagement.entity.Course;
import com.hau.ExamInvigilationManagement.entity.Department;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.CourseMapper;
import com.hau.ExamInvigilationManagement.repository.CourseRepository;
import com.hau.ExamInvigilationManagement.repository.DepartmentRepository;
import com.hau.ExamInvigilationManagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponse create(CourseRequest request) {
        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        Course course = Course.builder()
                .code(request.getCode())
                .name(request.getName())
                .department(dept)
                .build();

        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    public Page<CourseResponse> getAllWithPagination(Pageable pageable) {
        Page<Course> page = courseRepository.findAll(pageable);
        return page.map(courseMapper::toResponse);
    }

    @Override
    public Page<CourseResponse> searchByKeyword(String keyword, Pageable pageable) {
        Page<Course> page = courseRepository.searchByKeyword(keyword, pageable);
        return page.map(courseMapper::toResponse);
    }

    @Override
    public List<CourseResponse> getAll() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    public CourseResponse getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return courseMapper.toResponse(course);
    }

    @Override
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        course.setCode(request.getCode());
        course.setName(request.getName());
        course.setDepartment(dept);

        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCourses(Long departmentId, MultipartFile file) {
        // 1. Kiểm tra Khoa có tồn tại không
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            System.out.println("============== BẮT ĐẦU IMPORT HỌC PHẦN ==============");

            // Duyệt từ dòng 1 (bỏ dòng tiêu đề index 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 2. Đọc Mã HP (Cột Index 1)
                String code = getCellValue(row.getCell(1)).trim();
                if (code.isEmpty()) continue;

                // Kiểm tra trùng theo mã HP
                if (courseRepository.existsByCode(code)) {
                    System.err.println("Dòng " + (i+1) + ": Mã học phần " + code + " đã tồn tại - BỎ QUA");
                    continue;
                }

                // 3. Đọc Tên HP (Cột Index 2)
                String name = getCellValue(row.getCell(2)).trim();

                // 4. Tạo và Lưu Course
                Course course = Course.builder()
                        .code(code)
                        .name(name)
                        .department(department)
                        .build();

                courseRepository.save(course);
            }
            System.out.println("============== IMPORT HỌC PHẦN HOÀN TẤT ==============");
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    // Hàm phụ trợ đọc cell Excel
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Nếu là ngày, chuyển đổi sang LocalDate rồi format thành chuỗi dd/MM/yyyy
                    try {
                        return cell.getLocalDateTimeCellValue().toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (Exception e) {
                        return ""; // Phòng trường hợp lỗi convert
                    }
                }
                // Nếu là số bình thường (ví dụ: số lượng giám thị)
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Nếu là công thức, thử lấy kết quả string, nếu không được thì lấy số
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}

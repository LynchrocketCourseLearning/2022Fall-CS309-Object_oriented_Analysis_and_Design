let Obj = null;

function toggle(id, mod) {
    let table = document.getElementById(id);
    let cover = document.getElementById('cover');
    if (table.style.display === 'none' && mod === 'open') {
        table.style.display = 'block';
        table.style.opacity = '1';
        cover.style.display = '';
    }
    if (table.style.display === 'block' && mod === 'close') {
        table.style.display = 'none';
        cover.style.display = 'none';
    }
}

function showAddCourseTable() {
    toggle('addCourseTable', 'open');
    toggle('editCourse', 'close');
}

function onClickAddCourse() {
    if (validateInput('addcourse')) {
        addRow();
        alert("Success to add!");
        toggle('addCourseTable', 'close');
    }
}

function validateInput(mod) {
    let courseName; let courseCode; let langs; let teacherName; let date; let time; let locationId; let locations; let duration;
    if (mod === 'addcourse') {
        courseName = document.querySelector('form input[name="courseName"]').value;
        courseCode = document.querySelector('form input[name="courseCode"]').value;
        langs = document.getElementsByName("lang");
        teacherName = document.getElementById('teacher').value;
        date = document.getElementById('date').value;
        time = document.getElementById('time').value;
        locationId = document.getElementById('location').value;
        locations = document.getElementById('location').children;
        duration = document.getElementById('duration').value;
    } else if (mod === 'editcourse') {
        courseName = document.querySelector('form input[name="courseNameEdit"]').value;
        courseCode = document.querySelector('form input[name="courseCodeEdit"]').value;
        langs = document.getElementsByName("langEdit");
        teacherName = document.getElementById('teacherEdit').value;
        date = document.getElementById('dateEdit').value;
        time = document.getElementById('timeEdit').value;
        locationId = document.getElementById('locationEdit').value;
        locations = document.getElementById('locationEdit').children;
        duration = document.getElementById('durationEdit').value;
    }

    date = date.split('-');
    time = time.split(':');
    let location = locations[locationId].innerHTML.trim();
    duration += '.0';
    duration = duration.split('.');

    let plusHour = parseInt(duration[0]);
    let plusMinute = parseInt(duration[1]) * 60;

    let endHour = parseInt(time[0]) + plusHour + Math.floor((parseInt(time[1]) + plusMinute) / 60);
    let endMinute = (parseInt(time[1]) + plusMinute) % 60;

    let bodyObj = document.getElementById("tbody");
    if (!bodyObj) {
        alert("Body of Table not Exist!");
        return False;
    }

    if (courseName == "" || courseCode == "" || langs == "" || teacherName == "" || date == "" || time == "" || location == "" || duration == "") {
        alert("Null input exists.");
        return false;
    }

    let nameRegex = new RegExp(/^[A-Za-z, ]+$/);
    let courseCodeRegex = new RegExp(/^[A-Za-z0-9]+$/);
    if (!nameRegex.test(courseName)) {
        alert("Invalid course name.");
        return false;
    }
    if (!courseCodeRegex.test(courseCode)) {
        alert("Invalid course code.");
        return false;
    }
    if (!nameRegex.test(teacherName)) {
        alert("Invalid teacher name.");
        return false;
    }
    if (parseFloat(duration).toString() === "NaN") {
        alert("Invalid duration.");
        return false;
    }

    let dateNow = new Date();
    let yearNow = dateNow.getFullYear();
    let monthNow = dateNow.getMonth() + 1;
    let dayNow = dateNow.getDate();
    if (parseInt(date[0]) < yearNow) {
        alert("Invalid year.");
        return false;
    } else if (parseInt(date[0]) === yearNow) {
        if (parseInt(date[1]) < monthNow) {
            alert("Invalid month.");
            return false;
        } else if (parseInt(date[1]) === monthNow) {
            if (parseInt(date[2]) < dayNow) {
                alert("Invalid day.");
                return false;
            }
        }
    }

    let trList = bodyObj.children;
    if (trList.length === 1) {
        return true;
    }
    for (let i = 1; i < trList.length; i++) {
        if (mod === 'editcourse') {
            let parentTR = Obj.parentNode.parentNode;
            if (parentTR === trList[i]) {
                continue;
            }
        }
        let courseCodeTable = trList[i].cells[1].innerHTML;
        let teacherNameTable = trList[i].cells[3].innerHTML;
        let dateTable = trList[i].cells[4].innerHTML;
        dateTable = dateTable.split('/');
        let timeTable = trList[i].cells[5].innerHTML;
        timeTable = timeTable.split(':');
        let hour = parseInt(timeTable[0]);
        let minute = parseInt(timeTable[1]);
        let locationTable = trList[i].cells[6].innerHTML;
        let durationTable = trList[i].cells[7].innerHTML;
        durationTable = durationTable.substring(0, durationTable.length - 1);
        durationTable = durationTable.split('.');
        let plusHour = parseInt(durationTable[0]);
        let plusMinute = parseInt(durationTable[1]) * 60;

        let endHourTable = hour + plusHour + Math.floor((minute + plusMinute) / 60)
        let endMinuteTable = (minute + plusMinute) % 60

        if (date[0] === dateTable[0] && date[1] === dateTable[1] && date[2] === dateTable[2]) {
            if ((parseInt(time[0]) > hour && parseInt(time[0]) < endHourTable) || (hour > parseInt(time[0]) && hour < endHour)) {
                if (locationTable === location) {
                    alert("conflict location in same time.");
                    return false;
                }
            } else if (parseInt(time[0]) === hour || parseInt(time[0]) === endHourTable || endHour === hour || endHour === endHourTable) {
                if ((parseInt(time[1]) >= minute && parseInt(time[1]) <= endMinuteTable) || (minute >= parseInt(time[1]) && minute <= endMinute)) {
                    if (locationTable === location) {
                        alert("conflict location in same time.");
                        return false;
                    }
                }
            }
            if (courseCodeTable === courseCode) {
                alert("One course is scheduled at most once a day.");
                return false;
            }
            if (teacherNameTable === teacherName) {
                alert("Each teacher can take no more than one lecture per day.");
                return false;
            }
        }
    }
    return true;
}

function addRow() {
    let bodyObj = document.getElementById("tbody");
    if (!bodyObj) {
        alert("Body of Table not Exist!");
        return;
    }
    let courseName = document.querySelector('form input[name="courseName"]').value;
    let courseCode = document.querySelector('form input[name="courseCode"]').value;
    let langs = document.getElementsByName("lang");
    let teacherName = document.getElementById('teacher').value;
    let date = document.getElementById('date').value;
    date = date.split('-');
    let time = document.getElementById('time').value;
    time = time.split(':');
    let locationId = document.getElementById('location').value;
    let locations = document.getElementById('location').children;
    let location = locations[locationId].innerHTML;
    let duration = document.getElementById('duration').value;
    if (parseInt(duration) == duration) {
        duration = duration + '.0'
    }

    let rowCount = bodyObj.rows.length;
    let cellCount = bodyObj.rows[0].cells.length;

    bodyObj.style.display = ""; // display the tbody
    let newRow = bodyObj.insertRow(rowCount++);

    newRow.insertCell(0).innerHTML = courseName;
    newRow.insertCell(1).innerHTML = courseCode;
    newRow.insertCell(2).innerHTML = (langs[0].checked ? "Chinese" : (langs[1].checked ? "English" : "Bilingual"));
    newRow.insertCell(3).innerHTML = teacherName;
    newRow.insertCell(4).innerHTML = date[0] + '/' + date[1] + '/' + date[2];
    newRow.insertCell(5).innerHTML = time[0] + ':' + time[1];
    newRow.insertCell(6).innerHTML = location;
    newRow.insertCell(7).innerHTML = duration + 'h';
    newRow.insertCell(8).innerHTML = bodyObj.rows[0].cells[cellCount - 1].innerHTML; // copy the "delete" button
    bodyObj.rows[0].style.display = "none"; // hide first row
}

function showEditCourseTable(inputObj) {
    toggle('addCourseTable', 'close');
    toggle('editCourse', 'open');
    Obj = inputObj;

    let parentTR = inputObj.parentNode.parentNode;

    let courseName = parentTR.cells[0].innerHTML;
    let courseCode = parentTR.cells[1].innerHTML;
    let lang = parentTR.cells[2].innerHTML;
    let teacherName = parentTR.cells[3].innerHTML;
    let date = parentTR.cells[4].innerHTML;
    date = date.split('/')
    let time = parentTR.cells[5].innerHTML;
    let location = parentTR.cells[6].innerHTML;
    location = location.trim();
    let duration = parentTR.cells[7].innerHTML;

    document.querySelector('form input[name="courseNameEdit"]').value = courseName;
    document.querySelector('form input[name="courseCodeEdit"]').value = courseCode;
    let langEdit = document.getElementsByName("langEdit");
    if (lang === 'Chinese') {
        langEdit[0].checked = true;
    } else if (lang === 'English') {
        langEdit[1].checked = true;
    } else {
        langEdit[2].checked = true;
    }
    document.getElementById('teacherEdit').value = teacherName;
    document.getElementById('dateEdit').value = date[0] + '-' + date[1] + '-' + date[2];
    document.getElementById('timeEdit').value = time;
    if (location === 'Teaching Building No.1 Lecture Hall') {
        document.getElementById('locationEdit').value = 1;
    } else if (location === 'Research Building Lecture Hall') {
        document.getElementById('locationEdit').value = 2;
    } else {
        document.getElementById('locationEdit').value = 3;
    }
    document.getElementById('durationEdit').value = duration.substring(0, duration.length - 1);
}

function editRow() {
    if (!validateInput('editcourse')) {
        return;
    }
    let parentTR = Obj.parentNode.parentNode;

    let courseName = document.querySelector('form input[name="courseNameEdit"]').value;
    let courseCode = document.querySelector('form input[name="courseCodeEdit"]').value;
    let langs = document.getElementsByName("langEdit");
    let teacherName = document.getElementById('teacherEdit').value;
    let date = document.getElementById('dateEdit').value;
    date = date.split('-');
    let time = document.getElementById('timeEdit').value;
    time = time.split(':');
    let locationId = document.getElementById('locationEdit').value;
    let locations = document.getElementById('locationEdit').children;
    let location = locations[locationId].innerHTML;
    let duration = document.getElementById('durationEdit').value;

    parentTR.cells[0].innerHTML = courseName;
    parentTR.cells[1].innerHTML = courseCode;
    parentTR.cells[2].innerHTML = (langs[0].checked ? "Chinese" : (langs[1].checked ? "English" : "Bilingual"));
    parentTR.cells[3].innerHTML = teacherName;
    parentTR.cells[4].innerHTML = date[0] + '/' + date[1] + '/' + date[2];
    parentTR.cells[5].innerHTML = time[0] + ':' + time[1];
    parentTR.cells[6].innerHTML = location;
    parentTR.cells[7].innerHTML = duration + 'h';

    toggle('editCourse', 'close');
}

function removeRow(inputObj) {
    if (!inputObj) return;
    let parentTD = inputObj.parentNode;
    let parentTR = parentTD.parentNode;
    let parentTBODY = parentTR.parentNode;
    parentTBODY.removeChild(parentTR);
}

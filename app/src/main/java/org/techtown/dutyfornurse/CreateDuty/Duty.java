package org.techtown.dutyfornurse.CreateDuty;


import java.io.Serializable;
import java.util.Random;

public class Duty implements Serializable {
    public char[][] timetable;//전체 테이블
    public char[][] d_table;//주간근무 테이블
    public char[][] v_table;//숙련자 테이블
    public char[][] b_table;//비숙련자 테이블
    public int error_count=0;

    public Duty(int num,int daywork,int veteran,int begginer,int month) {//듀티 생성자
        this.timetable=new char[month][num];
        this.d_table=new char[month][daywork];
        this.v_table=new char[month][veteran];
        this.b_table=new char[month][begginer];
    }

    public void setTimetable(int i,int j,char time){
        this.timetable[i][j]=time;
    }





    public int DutyMake(int v_Day,int v_Evening,int v_Night,int b_Day,int b_Evening,int b_Night,int Min_off,boolean option1,boolean option2,boolean option3,boolean option4,boolean option5,boolean option6,Duty save_duty) {//듀티 생성 함수

        int num=v_table[0].length+b_table[0].length;

        Random rand = new Random();
        int v_Off = v_table[0].length - v_Day - v_Evening - v_Night;
        int b_Off = b_table[0].length - b_Day - b_Evening - b_Night;
        boolean ErrorCode = false;

        int[] night_limit = new int[num];
        int[] off_limit = new int[num];
        if(v_table[0].length != 0) {
            int v_all_night_number = timetable.length * v_Night;//전체 나이트 개수=일수*1일당 나이트개수
            int v_personal_night_number = v_all_night_number / v_table[0].length;//개인당 나이트 개수 =전체나이트개수/인원 개수
            int v_remain_night_number = v_all_night_number - (v_personal_night_number * v_table[0].length);//나머지 나이트개수= 전체나이트개수-(개인당 나이트개수*인원개수)
            int[] number=new int[v_remain_night_number + (v_table[0].length / 3)];

            int v_all_off_number= timetable.length * v_Off;
            int v_personal_off_number = v_all_off_number/v_table[0].length;




            for (int i = 0; i < v_table[0].length; i++) {
                night_limit[i] = (v_personal_night_number);
                off_limit[i]=(v_personal_off_number+1)+(v_personal_off_number-Min_off);
            }
            for (int i = 0; i < v_remain_night_number + (v_table[0].length/3); i++) {
                boolean n=false;
                if(i==v_table[0].length){
                    for(int k=0;k<v_table[0].length;k++)
                        number[k]='\0';
                }
                number[i] = rand.nextInt(v_table[0].length);
                for(int j=0;j<i;j++){
                    if(number[i]==number[j]) {
                        i--;
                        n=true;
                    }

                }
                if(n==false)
                    night_limit[number[i]]++;
            }
        }


        if(b_table[0].length!=0) {
            int b_all_night_number = timetable.length * b_Night;//전체 나이트 개수=일수*1일당 나이트개수
            int b_personal_night_number = b_all_night_number / b_table[0].length;//개인당 나이트 개수 =전체나이트개수/인원 개수
            int b_remain_night_number = b_all_night_number - (b_personal_night_number * b_table[0].length);//나머지 나이트개수= 전체나이트개수-(개인당 나이트개수*인원개수)
            int[] number=new int[b_remain_night_number + (b_table[0].length / 3)];

            int b_all_off_number= timetable.length * b_Off;
            int b_personal_off_number = b_all_off_number/b_table[0].length;

            for (int i = v_table[0].length; i < num; i++) {
                night_limit[i] = (b_personal_night_number);
                off_limit[i]=(b_personal_off_number+1)+(b_personal_off_number-Min_off);
            }
            for (int i = 0; i < b_remain_night_number + (b_table[0].length / 3); i++) {
                boolean n=false;
                if(i==b_table[0].length){
                    for(int k=0;k<b_table[0].length;k++)
                        number[k]='\0';
                }
                number[i] = rand.nextInt(b_table[0].length)+v_table[0].length;
                for(int j=0;j<i;j++){
                    if(number[i]==number[j]) {
                        i--;
                        n=true;
                    }
                }
                if(n==false)
                    night_limit[number[i]]++;
            }
        }

        int[] day_count=new int [num];//데이 연속 숫자
        int[] evening_count=new int[num];//이브닝 연속 숫자
        int[] night_count=new int[num];//나이트 연속 숫자
        int[] off_count=new int[num];//오프 연속 숫자

        int[] personal_night_count=new int[num];//개인당 나이트 숫자
        int[] work_count=new int[num];//근무 연속 숫자

        int[] personal_off_count=new int[num];//개인당 오프 숫자
        char[] recent_work=new char[num];//바로 전날 쉬프트 저장
        boolean[] two_off=new boolean[num];//연속 나이트후 2off 변수
        int[] priority=new int[num];//우선순위 저장
        char[] application=new char[num];//신청근무 확인
        char[] next_application=new char[num];



        char work;
        int Duty_count=0;





        for(int i=0;i<timetable.length;i++)//한달 반복
        {
            boolean[] execution = new boolean[num];
            ErrorCode = false;
            int D_count = 0;
            int E_count = 0;
            int N_count = 0;
            int O_count = 0;

            boolean day;
            boolean evening;
            boolean night;
            boolean off;

            int sucess_count = 0;
            for (int k = 0; k < 4; k++) {//priority 검사 알고리즘(0~3까지 오름차 순으로 검사)
                if (ErrorCode == true) {//조건에 맞는 듀티가 없을시
                    error_count++;//에러카운트1증가
                    //System.out.println(error_count + " ");
                    if (error_count > 50000000)//에러카운트 5000이상일시 금지
                        return 0;
                    i = -1;//다시 처음부터 반복
                    Duty_count++;
                    for (int p = 0; p < v_table[0].length; p++) {//다시 처음으로 리셋
                        priority[p] = 0;
                        night_count[p] = 0;
                        work_count[p] = 0;
                        two_off[p] = false;
                        personal_night_count[p] = 0;
                        personal_off_count[p]=0;
                        recent_work[p] = '\0';
                        application[p]='\0';
                        next_application[p]='\0';
                    }
                    break;
                }
                if (sucess_count == v_table[0].length) {//숙련자 테이블에 듀티를 다 채웠다면 stop
                    break;
                }

                //숙련자 타임테이블
                for (int j = 0; j < v_table[0].length; j++) {//베테랑 타임테이블 듀티
                    if (execution[j] == false) {//아직 생성이 안되어있을시
                        application[j]=save_duty.v_table[i][j];
                        if(i<timetable.length-1)
                            next_application[j]=save_duty.v_table[i+1][j];

                        day = false;
                        evening = false;
                        night = false;
                        off = false;

                        if (D_count == v_Day) //하루 데이 인원 충족
                            day = true;
                        if (E_count == v_Evening)//하루 이브닝 인원 충족
                            evening = true;
                        if (N_count == v_Night)//하루 나이트 인원 충족
                            night = true;
                        if (O_count == v_Off)//하루 오프 인원 충족
                            off = true;

                        if (i != 0) {// receunt_work 첫날이 아닐시 전날의 근무 가져옴
                            if (recent_work[j] == 'E' && option2 == true)
                                day = true;
                            if (recent_work[j] == 'N' && option1 == true) {
                                day = true;
                                evening = true;
                            }
                        }
                        if (night_count[j] == 1 ) {//night_count  나이트 전날에 한번했으면 나이트 한번더(option1)
                            if(option1==true) {
                                day = true;
                                evening = true;
                            }
                            if(option5==true)
                                two_off[j]=true;
                            if(option6==true) {
                                day=true;
                                evening=true;
                                off = true;
                            }

                        } else if (night_count[j] == 2) {//나이트 두번했으면 한번더나이트 이거나 2off
                            if(option1==true) {
                                day = true;
                                evening = true;
                            }
                            /*if (work_count[j] != 2)
                                night = true;*/
                            if (option5 == true)
                                two_off[j] = true;
                        } else if (night_count[j] == 3) {//나이트 세번했으면  2off
                            day = true;
                            evening = true;
                            night = true;
                            if (option5 == true)
                                two_off[j] = true;
                        }

                        if (two_off[j] == true ) {//two_off,off 한번더
                            if(recent_work[j]=='O') {
                                day = true;
                                evening = true;
                                night = true;

                                two_off[j] = false;
                            }
                            if(recent_work[j]=='D' || recent_work[j]=='E'){
                                two_off[j]=false;
                            }
                        }

                        if (work_count[j] == 1 && option4 == true && i>1) //근무는 최소 2번이상(option4)
                            off = true;


                        if (work_count[j] >= 4 && option3 == true) {//4일연속 근무했으면 off(option3)
                            day = true;
                            evening = true;
                            night = true;
                        }
                        if (work_count[j] >= 5) {//5일연속 근무했으면 off 두번이상
                            day = true;
                            evening = true;
                            night = true;
                            two_off[j] = true;
                        }
                        if (off_count[j] >= 4)//off 연속 4번이상했을시 off금지
                            off = true;

                        if (day_count[j] >= 3) {
                            day = true;
                        }

                        if (evening_count[j] >= 3) {
                            evening = true;
                        }
                        if (personal_night_count[j] >= night_limit[j]) {
                            night = true;
                        }
                        if(next_application[j]=='D'){
                            if(option2==true)
                                evening=true;
                            if(option1==true)
                                night=true;
                            if(work_count[j]>=4 || option3==true){
                                day=true;
                                evening=true;
                                night=true;
                            }
                        }
                        if(next_application[j]=='E') {
                            if (option1 == true)
                                night = true;
                            if (work_count[j] >= 4 || option3 == true) {
                                day = true;
                                evening = true;
                                night = true;
                            }
                        }
                        if (next_application[j] == 'O') {
                            night = true;
                        }
                        if(personal_off_count[j] >= off_limit[j]){
                            off=true;
                        }

                        if (CheckPriority(day, evening, night, off) == k || CheckPriority(day, evening, night, off) == -1 || i == 0)//첫째줄이거나, priority 차례가 되었을시
                        {
                            execution[j] = true;//생성확인 배열
                            k = -1;
                            sucess_count++;
                            if(application[j]=='D' || application[j]=='E' ||application[j]=='N' ||application[j]=='O' )
                                work=application[j];
                            else
                                work = DrawWork(day, evening, night, off);//work생성
                            recent_work[j] = work;//recent_work 업데이트


                            if (work == 'D') {//생성된 work가 D일떼
                                D_count++;
                                work_count[j]++;
                                night_count[j]=0;
                                off_count[j] = 0;
                                day_count[j]++;
                                priority[j] = 3;//디폴트값은 우선순위 3=다가능
                                if(personal_off_count[j] >=off_limit[j])
                                    priority[j]=2;
                                if (personal_night_count[j] == night_limit[j]) {
                                    priority[j] = 2;
                                    if(personal_off_count[j] >=off_limit[j])
                                        priority[j]=1;
                                }
                                if (work_count[j] == 1 && option4 == true) {
                                    priority[j] = 2;
                                    if (personal_night_count[j] == night_limit[j])
                                        priority[j] = 1;
                                }
                                if (work_count[j] == 5 && option3 == true)
                                    priority[j] = 0;
                                if (day_count[j] >= 3) {
                                    priority[j] = 2;
                                    if(personal_off_count[j] >=off_limit[j])
                                        priority[j]=1;
                                    if (personal_night_count[j] == night_limit[j]) {
                                        priority[j] = 1;
                                        priority[j]=0;
                                    }
                                }
                            } else if (work == 'E') {//생성된 work가 E일떼
                                E_count++;
                                work_count[j]++;
                                night_count[j]=0;
                                off_count[j] = 0;
                                day_count[j] = 0;
                                evening_count[j]++;
                                priority[j] = 3;//아무조건없으면 다가능
                                if(personal_off_count[j] >=off_limit[j])
                                    priority[j]=2;
                                if (personal_night_count[j] == night_limit[j]) {
                                    priority[j] = 2;
                                    if(personal_off_count[j] >=off_limit[j])
                                        priority[j]=1;
                                }
                                if (option2 == true) {//ED근무 제외 조건있을시 D는 불가능
                                    priority[j] = 2;
                                    if(personal_off_count[j] >=off_limit[j])
                                        priority[j]=1;
                                    if (personal_night_count[j] == night_limit[j]) {
                                        priority[j] = 1;
                                        if (personal_off_count[j] >= off_limit[j])
                                            priority[j]=0;
                                    }
                                }
                                if (work_count[j] == 1 && option4 == true) {
                                    priority[j] = 1;
                                    if (personal_night_count[j] == night_limit[j])
                                        priority[j] = 0;
                                }
                                if (work_count[j] == 5 && option3 == true)
                                    priority[j] = 0;
                                if (evening_count[j] >= 3) {
                                    priority[j] = 1;
                                    if(personal_off_count[j] >=off_limit[j])
                                        priority[j]=0;
                                    if (personal_night_count[j] == night_limit[j])
                                        priority[j] = 0;
                                }
                            } else if (work == 'N') {//생성된 work가 N일때
                                N_count++;
                                night_count[j]++;
                                off_count[j] = 0;
                                day_count[j] = 0;
                                evening_count[j] = 0;
                                work_count[j]++;
                                priority[j] = 1;
                                personal_night_count[j]++;
                                if (personal_off_count[j] >= off_limit[j])
                                    priority[j] = 0;
                                if (personal_night_count[j] == night_limit[j])
                                    priority[j] = 0;
                                if (night_count[j] == 1 && option5 == true){//night옵션체크시 night1번일땐 night
                                    priority[j] = 1;
                                    if(option6==true)
                                        priority[j]=0;
                                }
                                if (night_count[j] == 3 && option5 == true)//night옵션체크시 night3번일땐 off
                                    priority[j] = 0;
                                if (night_count[j] == 2 && option5 == true) {//night옵션체크시 night2번일땐 night,off
                                    priority[j] = 1;
                                    if(personal_off_count[j] >=off_limit[j])
                                        priority[j]=0;
                                    if (personal_night_count[j] == night_limit[j])
                                        priority[j] = 0;
                                }
                                if (night_count[j] == 2 && work_count[j] != 2)
                                    priority[j] = 0;
                                if (work_count[j] == 5 && option3 == true)//퐁당퐁당옵션체크시 5일연속근무시에 off
                                    priority[j] = 0;
                            } else if (work == 'O') {//생성된 work가 O일때
                                O_count++;
                                personal_off_count[j]++;
                                night_count[j] = 0;
                                work_count[j] = 0;
                                day_count[j] = 0;
                                evening_count[j] = 0;
                                off_count[j]++;
                                priority[j] = 3;
                                if (personal_night_count[j] == night_limit[j]) {
                                    priority[j] = 2;
                                    if(personal_off_count[j] >=off_limit[j])
                                        priority[j]=1;
                                }
                                if (two_off[j] == true)//two_off조건이 활성화될시 0순위
                                    priority[j] = 0;
                                if (off_count[j] >= 4) {//4번이상off시 2순위
                                    priority[j] = 2;
                                    if (personal_night_count[j] == night_limit[j])
                                        priority[j] = 1;
                                }
                            } else {//조건에맞는 work가 없을시
                                ErrorCode = true;//에러코드 발생
                            }

                            v_table[i][j] = work;
                            break;
                        }
                    }
                }
            }
            if(i==timetable.length-1) {
                for (int k = 0; k < v_table[0].length; k++) {
                    if (personal_off_count[k] < Min_off) {//조건에 맞는 듀티가 없을시
                        error_count++;//에러카운트1증가
                        //System.out.println(error_count + " ");
                        if (error_count > 50000000)//에러카운트 5000이상일시 금지
                            return 0;
                        i = -1;//다시 처음부터 반복
                        Duty_count++;
                        for (int p = 0; p < v_table[0].length; p++) {//다시 처음으로 리셋
                            priority[p] = 0;
                            night_count[p] = 0;
                            work_count[p] = 0;
                            two_off[p] = false;
                            personal_night_count[p] = 0;
                            personal_off_count[p] = 0;
                            recent_work[p] = '\0';
                            application[p]='\0';
                            next_application[p]='\0';

                        }
                    }
                }
            }
        }





        //v->b 체인지(비숙련자 타임테이블)(숙련자 테이블과 코드는 동일함)
        for(int i=0;i<timetable.length;i++)//한달 반복
        {
            boolean[] execution = new boolean[num];
            ErrorCode = false;
            int D_count = 0;
            int E_count = 0;
            int N_count = 0;
            int O_count = 0;

            boolean day;
            boolean evening;
            boolean night;
            boolean off;

            int sucess_count = 0;
            for (int k = 0; k < 4; k++) {//priority 검사 알고리즘 (0~3까지 오름차 순으로 검사)
                if (ErrorCode == true) {//조건에 맞는 듀티가 없을시
                    error_count++;//에러카운트1증가
                    ////System.out.println(error_count + " ");
                    if (error_count > 50000000)//에러카운트 5000이상일시 금지
                        return 0;
                    i = -1;//다시 처음부터 반복
                    Duty_count++;
                    for (int p = v_table[0].length; p <num; p++) {//다시 처음으로 리셋
                        priority[p] = 0;
                        night_count[p] = 0;
                        work_count[p] = 0;
                        two_off[p] = false;
                        personal_night_count[p] = 0;
                        personal_off_count[p]=0;
                        recent_work[p] = '\0';
                        application[p]='\0';
                        next_application[p]='\0';

                    }
                    break;
                }
                if (sucess_count == b_table[0].length) {//비숙련자 테이블에 듀티를 다 채웠다면 stop
                    break;
                }


                for (int j = 0; j < b_table[0].length; j++) {//비숙련자 타임테이블
                    int j_p = j + v_table[0].length;
                    if (execution[j_p] == false) {//아직 생성이 안되어 있을시
                        application[j_p]=save_duty.b_table[i][j];
                        if(i<timetable.length-1)
                            next_application[j_p]=save_duty.b_table[i+1][j];

                        day = false;
                        evening = false;
                        night = false;
                        off = false;

                        if (D_count == (b_Day)) //하루 데이 인원 충족
                            day = true;
                        if (E_count == (b_Evening))//하루 이브닝 인원 충족
                            evening = true;
                        if (N_count == (b_Night))//하루 나이트 인원 충족
                            night = true;
                        if (O_count == (b_Off))//하루 오프 인원 충족
                            off = true;

                        if (i != 0) {// receunt_work 첫날이 아닐시 전날의 근무 가져옴
                            if (recent_work[j_p] == 'E' && option2 == true)
                                day = true;
                            if (recent_work[j_p] == 'N' && option1 == true) {
                                day = true;
                                evening = true;
                            }
                        }

                        if (night_count[j_p] == 1 ) {//night_count  나이트 전날에 한번했으면 나이트 한번더
                            if(option1==true) {
                                day = true;
                                evening = true;
                            }
                            if(option5==true) {
                                two_off[j_p] = true;
                            }
                            if(option6==true) {
                                day=true;
                                evening=true;
                                off = true;
                            }
                        } else if (night_count[j_p] == 2) {//나이트 두번했으면 한번더하거나 2off
                            if (option1 == true) {
                                day = true;
                                evening = true;
                            }

                            if (option5 == true)
                                two_off[j_p] = true;
                        } else if (night_count[j_p] == 3) {//나이트 세번했으면  2off
                            day = true;
                            evening = true;
                            night = true;
                            if (option5 == true)
                                two_off[j_p] = true;
                        }

                        if (two_off[j_p] == true ) {//two_off 연속 나이트 이후엔 off두번이상
                            if(recent_work[j_p]=='O') {
                                day = true;
                                evening = true;
                                night = true;

                                two_off[j_p]=false;
                            }
                            if(recent_work[j_p]=='D' || recent_work[j_p]=='E')
                                two_off[j_p] = false;
                        }
                        if (work_count[j_p] == 1 && option4 == true && i>1)//근무는 최소 2번이상(option4)
                            off = true;


                        if (work_count[j_p] >= 4 && option3 == true) {//4일연속 근무했으면 off(option3)
                            day = true;
                            evening = true;
                            night = true;
                        }
                        if (work_count[j_p] >= 5) {//5일연속 근무했으면 off 두번이상 (option3)
                            day = true;
                            evening = true;
                            night = true;
                            two_off[j_p] = true;
                        }

                        if (off_count[j_p] >= 4)//off 연속 4번이상했을시 off금지
                            off = true;

                        if (day_count[j_p] >= 3) {
                            day = true;
                        }
                        if (evening_count[j_p] >= 3) {
                            evening = true;
                        }
                        if (personal_night_count[j_p] >= night_limit[j_p]) {
                            night = true;
                        }
                        if(next_application[j_p]=='D'){
                            if(option2==true)
                                evening=true;
                            if(option1==true)
                                night=true;
                            if(work_count[j_p]>=4 || option3==true){
                                day=true;
                                evening=true;
                                night=true;
                            }
                        }
                        if(next_application[j_p]=='E') {
                            if (option1 == true)
                                night = true;
                            if (work_count[j_p] >= 4 || option3 == true) {
                                day = true;
                                evening = true;
                                night = true;
                            }
                        }
                        if (next_application[j_p] == 'O') {
                            night = true;
                        }
                        if(personal_off_count[j_p] >= off_limit[j_p]){
                            off=true;
                        }



                        if (CheckPriority(day, evening, night, off) == k || CheckPriority(day, evening, night, off) == -1 || i == 0)//첫째줄이거나, priority 차례가 되었을시
                        {
                            execution[j_p] = true;//생성확인 배열
                            k = -1;
                            sucess_count++;
                            if(application[j_p]=='D' || application[j_p]=='E' ||application[j_p]=='N' ||application[j_p]=='O' )
                                work=application[j_p];
                            else
                                work = DrawWork(day, evening, night, off);//work생성
                            recent_work[j_p] = work;//recent_work 업데이트

                            if (work == 'D') {//생성된 work가 D일때
                                D_count++;
                                work_count[j_p]++;
                                night_count[j_p]=0;
                                off_count[j_p] = 0;
                                day_count[j_p]++;
                                priority[j_p] = 3;//디폴트값은 우선순위 3=다가능
                                if(personal_off_count[j_p] >=off_limit[j_p])
                                    priority[j_p]=2;
                                if (personal_night_count[j_p] == night_limit[j_p]) {
                                    priority[j_p] = 2;
                                    if(personal_off_count[j_p] >=off_limit[j_p])
                                        priority[j_p]=1;
                                }

                                if (work_count[j_p] == 1 && option4 == true) {
                                    priority[j_p] = 2;
                                    if (personal_night_count[j_p] == night_limit[j_p])
                                        priority[j_p] = 1;
                                }
                                if (work_count[j_p] == 5 && option3 == true)
                                    priority[j_p] = 0;
                                if (day_count[j_p] >= 3) {
                                    priority[j_p] = 2;
                                    if(personal_off_count[j_p] >=off_limit[j_p])
                                        priority[j_p]=1;
                                    if (personal_night_count[j_p] == night_limit[j_p]) {
                                        priority[j_p] = 1;
                                        priority[j_p]=0;
                                    }
                                }
                            } else if (work == 'E') {//생성된 work가 E일때
                                E_count++;
                                work_count[j_p]++;
                                night_count[j_p]=0;
                                off_count[j_p] = 0;
                                day_count[j_p] = 0;
                                evening_count[j_p]++;
                                priority[j_p] = 3;//아무조건없으면 다가능
                                if(personal_off_count[j_p] >=off_limit[j_p])
                                    priority[j_p]=2;
                                if (personal_night_count[j_p] == night_limit[j_p]) {
                                    priority[j_p] = 2;
                                    if(personal_off_count[j_p] >=off_limit[j_p])
                                        priority[j_p]=1;
                                }
                                if (option2 == true) {//ED근무 제외 조건있을시 D는 불가능
                                    priority[j_p] = 2;
                                    if(personal_off_count[j_p] >=off_limit[j_p])
                                        priority[j_p]=1;
                                    if (personal_night_count[j_p] == night_limit[j_p]) {
                                        priority[j_p] = 1;
                                        if (personal_off_count[j_p] >= off_limit[j_p])
                                            priority[j_p]=0;
                                    }
                                }
                                if (work_count[j_p] == 1 && option4 == true) {
                                    priority[j_p] = 1;
                                    if (personal_night_count[j_p] == night_limit[j_p])
                                        priority[j_p] = 0;
                                }
                                if (work_count[j_p] == 5 && option3 == true)
                                    priority[j_p] = 0;
                                if (evening_count[j_p] >= 3) {
                                    priority[j_p] = 1;
                                    if(personal_off_count[j_p] >=off_limit[j_p])
                                        priority[j_p]=0;
                                    if (personal_night_count[j_p] == night_limit[j_p])
                                        priority[j_p] = 0;
                                }
                            } else if (work == 'N') {//생성된 work가 N일때
                                N_count++;
                                night_count[j_p]++;
                                off_count[j_p] = 0;
                                day_count[j_p] = 0;
                                evening_count[j_p] = 0;
                                work_count[j_p]++;
                                priority[j_p] = 1;
                                personal_night_count[j_p]++;
                                if(personal_off_count[j_p] >=off_limit[j_p])
                                    priority[j_p]=0;
                                if (personal_night_count[j_p] == night_limit[j_p])
                                    priority[j_p] = 0;
                                if (night_count[j_p] == 1 && option5 == true) {//night옵션체크시 night1번일땐 night
                                    priority[j_p] = 1;
                                    if(option6=true)
                                        priority[j_p]=0;
                                }
                                if (night_count[j_p] == 3 && option5 == true)//night옵션체크시 night3번일땐 off
                                    priority[j_p] = 0;
                                if (night_count[j_p] == 2 && option5 == true) {//night옵션체크시 night2번일땐 night,off
                                    priority[j_p] = 1;
                                    if(personal_off_count[j_p] >=off_limit[j_p])
                                        priority[j_p]=0;
                                    if (personal_night_count[j_p] == night_limit[j_p])
                                        priority[j_p] = 0;
                                }
                                if (night_count[j_p] == 2 && work_count[j_p] != 2)
                                    priority[j_p] = 0;
                                if (work_count[j_p] == 5 && option3 == true)//퐁당퐁당옵션체크시 5일연속근무시에 off
                                    priority[j_p] = 0;
                            } else if (work == 'O') {//생성된 work가 O일때
                                O_count++;
                                personal_off_count[j_p]++;
                                night_count[j_p] = 0;
                                work_count[j_p] = 0;
                                off_count[j_p]++;
                                day_count[j_p] = 0;
                                evening_count[j_p] = 0;
                                priority[j_p] = 3;
                                if (personal_night_count[j_p] == night_limit[j_p]) {
                                    priority[j_p] = 2;
                                    if(personal_off_count[j_p] >=off_limit[j_p])
                                        priority[j_p]=1;
                                }
                                if (two_off[j_p] == true)//two_off조건이 활성화될시 0순위
                                    priority[j_p] = 0;
                                if (off_count[j_p] >= 4) {//4번이상off시 2순위
                                    priority[j_p] = 2;
                                    if (personal_night_count[j_p] == night_limit[j_p])
                                        priority[j_p] = 1;
                                }
                            } else {//조건에 맞는  work가 없을시
                                ErrorCode = true;//에러코드 발생
                            }
                            b_table[i][j] = work;
                            break;
                        }
                    }
                }
            }
            if(i==timetable.length-1) {
                for (int k = v_table[0].length; k < num; k++) {
                    if (personal_off_count[k] < Min_off) {//조건에 맞는 듀티가 없을시
                        error_count++;//에러카운트1증가
                        //System.out.println(error_count + " ");
                        if (error_count > 50000000)//에러카운트 5000이상일시 금지
                            return 0;
                        i = -1;//다시 처음부터 반복
                        Duty_count++;
                        for (int p = v_table[0].length; p < num; p++) {//다시 처음으로 리셋
                            priority[p] = 0;
                            night_count[p] = 0;
                            work_count[p] = 0;
                            two_off[p] = false;
                            personal_night_count[p] = 0;
                            personal_off_count[p] = 0;
                            recent_work[p] = '\0';
                            application[p]='\0';
                            next_application[p]='\0';

                        }
                    }
                }
            }
        }


        PrintTable(timetable,v_table,b_table,Min_off);
        /*if(ErrorCode==true) {
            DutyMake(v_Day,v_Evening,v_Night,b_Day,b_Evening,b_Night,Min_off,option1,option2,option3,option4,option5,save_duty);
        }*/
        return 0;
    }



    public void PrintTable(char[][] timetable,char[][]v_table,char[][]b_table,int Max_off) {//숙련자 테이블+비숙련자 테이블 함수,전체 타임테이블 생성

        int[][] count=new int[timetable[0].length][4];// D->E->N->E 순으로 저장


        for(int i=0;i<timetable[0].length;i++) {
        }
        for(int i=0;i<timetable.length;i++) {//세로


            for(int j=0;j<d_table[0].length;j++){
                timetable[i][j]=d_table[i][j];
                if(timetable[i][j]=='D')
                    count[j][0]++;
                else if(timetable[i][j]=='E')
                    count[j][1]++;
                else if(timetable[i][j]=='N')
                    count[j][2]++;
                else if(timetable[i][j]=='O')
                    count[j][3]++;
                else {}

            }


            for(int j=0;j<v_table[0].length;j++) {
                timetable[i][j+d_table[0].length]=v_table[i][j];
                if(timetable[i][j+d_table[0].length]=='D')
                    count[j][0]++;
                else if(timetable[i][j+d_table[0].length]=='E')
                    count[j][1]++;
                else if(timetable[i][j+d_table[0].length]=='N')
                    count[j][2]++;
                else if(timetable[i][j+d_table[0].length]=='O')
                    count[j][3]++;
                else {}

            }


            for(int j=0; j<b_table[0].length; j++) {//가로
                timetable[i][j+d_table[0].length+v_table[0].length]=b_table[i][j];
                if(timetable[i][j+d_table[0].length+v_table[0].length]=='D')
                    count[j+d_table[0].length+v_table[0].length][0]++;
                else if(timetable[i][j+d_table[0].length+v_table[0].length]=='E')
                    count[j+d_table[0].length+v_table[0].length][1]++;
                else if(timetable[i][j+d_table[0].length+v_table[0].length]=='N')
                    count[j+d_table[0].length+v_table[0].length][2]++;
                else if(timetable[i][j+d_table[0].length+v_table[0].length]=='O')
                    count[j+d_table[0].length+v_table[0].length][3]++;
                else {}
            }
        }
    }







    public char DrawWork(boolean day,boolean evening,boolean night,boolean off) {
        Random rand=new Random();
        int work;

        if(!day && !evening && !night && !off) {//XXXX
            work=rand.nextInt(5);
            return (work == 0 || work == 3) ? 'D' : (work == 1) ? 'E' : (work == 2) ? 'N' : 'O';
        }

        if(day && !evening && !night && !off) {//OXXX
            work=rand.nextInt(3);
            return (work == 0) ? 'E' : (work == 1) ? 'N' : 'O';
        }

        if(!day && evening && !night && !off) {//XOXX
            work=rand.nextInt(4);
            return (work == 0 || work == 2) ? 'D' : (work == 1) ? 'N' : 'O';
        }

        if(!day && !evening && night && !off) {//XXOX
            work=rand.nextInt(4);
            return (work == 0 || work == 2) ? 'D' : (work == 1) ? 'E' : 'O';
        }

        if(!day && !evening && !night && off) {//XXXO
            work=rand.nextInt(4);
            return (work == 0 || work == 2) ? 'D' : (work == 1) ? 'E' : 'N';
        }

        if(day && evening && !night && !off) {//OOXX
            work=rand.nextInt(2);
            return (work == 0) ? 'N' : 'O';
        }

        if(day && !evening && night && !off) {//OXOX
            work=rand.nextInt(2);
            return (work == 0) ? 'E' : 'O';
        }

        if(day && !evening && !night && off) {//OXXO
            work=rand.nextInt(2);
            return (work == 0) ? 'E' : 'N';
        }

        if(!day && evening && night && !off) {//XOOX
            work=rand.nextInt(2);
            return (work == 0) ? 'D' : 'O';
        }

        if(!day && evening && !night && off) {//XOXO
            work=rand.nextInt(2);
            return (work == 0) ? 'D' : 'N';
        }

        if(!day && !evening && night && off) {//XXOO
            work=rand.nextInt(2);
            return (work == 0) ? 'D' : 'E';
        }

        if(day && evening && !night && off) {//OOXO
            return 'N';
        }

        if(day && !evening && night && off) {//OXOO
            return 'E';
        }

        if(!day && evening && night && off) {//XOOO
            return 'D';
        }

        if(day && evening && night && !off) {//OOOX
            return 'O';
        }
        return ' ';//오류일때

    }

    public int CheckPriority(boolean day,boolean evening,boolean night,boolean off) {
        int count=-1;
        if(day==false)
            count++;
        if(evening==false)
            count++;
        if(night==false)
            count++;
        if(off==false)
            count++;
        return count;
    }

}
package com.example.data

/**
 * Complete BWS 5-Day Full Body training program data, exercises, and PDF alternatives.
 */
object PlanData {

    data class Exercise(
        val id: String,
        val name: String,
        val persianName: String,
        val defaultSets: Int,
        val reps: String,
        val restSeconds: Int,
        val page: Int,
        val muscle: String,
        val note: String,
        val instructions: List<String>
    )

    data class DayPlan(
        val dayNumber: Int,
        val title: String,
        val subtitle: String,
        val exercises: List<Exercise>
    )

    data class Alternative(
        val name: String,
        val videoUrl: String = ""
    )

    val ALTERNATIVES: Map<String, List<Alternative>> = mapOf(
        "back-squat" to listOf(
            Alternative("Quad-Focused Leg Press", "https://youtu.be/0nrW-q7-WRQ"),
            Alternative("Smith Machine Squat", "https://youtu.be/zSVi51Jp3eI"),
            Alternative("Barbell Back Box Squat (knee friendly)", "https://youtu.be/QryQO4VuPK8"),
            Alternative("Weighted Step-Ups (knee friendly)", "https://youtu.be/Cjc3AgmdtlA"),
            Alternative("Dumbbell Goblet Squat (lower back friendly)", "https://youtu.be/nYDEYFXN2Rs"),
            Alternative("Bulgarian Split Squat (quad-focused)", "https://youtu.be/r9XtxWSTlcg")
        ),
        "low-incline-db-press" to listOf(
            Alternative("Incline Machine Chest Press", "https://youtu.be/abc1fisYB3w"),
            Alternative("Low Incline Smith Machine Press", "https://youtu.be/R53nThQcdZo"),
            Alternative("Low Incline Barbell Press", "https://youtu.be/jW4j7FoqudI"),
            Alternative("Low Incline Cable Press", "https://youtu.be/6qV1WZ_z0u0"),
            Alternative("(Banded) Decline Push-Ups", "https://youtu.be/LdahU9kB-u0")
        ),
        "seated-leg-curl" to listOf(
            Alternative("Lying Leg Curls", "https://youtu.be/aYy3alWRDmk"),
            Alternative("Swiss Ball Leg Curls", "https://youtu.be/uRBpd65dbYs"),
            Alternative("Dumbbell Lying Leg Curls", "https://youtu.be/Ot1MZipNLOQ")
        ),
        "lat-pulldown" to listOf(
            Alternative("(Weighted) Pull-Ups", "https://youtu.be/w_yuTRQd6HA"),
            Alternative("Kneeling One Arm Lat Pulldown", "https://youtu.be/PkA-D-Ld4tE"),
            Alternative("3 Point Dumbbell Row", "https://youtu.be/hrBLmuQ_vq8"),
            Alternative("Barbell Row (lat focused)", "https://youtu.be/tS5lKXxtNvE")
        ),
        "behind-body-curl" to listOf(
            Alternative("Incline Dumbbell Curls", "https://youtu.be/3D56VDVkQnM"),
            Alternative("Barbell Curl", "https://youtu.be/-ClfZ00zo8c")
        ),
        "bench-press" to listOf(
            Alternative("Flat Dumbbell Press", "https://youtu.be/g14dhC5KYBM"),
            Alternative("Flat Machine Chest Press", "https://youtu.be/sO8lFa9CidE"),
            Alternative("Flat Smith Machine Chest Press", "https://youtu.be/3Z3C44SXSQE"),
            Alternative("Seated Flat Cable Press", "https://youtu.be/hPpNTAEDnxM"),
            Alternative("Neutral Grip Dumbbell Press (shoulder friendly)", "https://youtu.be/N-kUwH1uf9c")
        ),
        "rdl" to listOf(
            Alternative("Dumbbell Romanian Deadlift", "https://youtu.be/Xu4DxwKWzl4"),
            Alternative("Hyperextensions (back/hamstring focused)", "https://youtu.be/RU5d2H_OmSc")
        ),
        "db-chest-row" to listOf(
            Alternative("Barbell Row (mid/upper back focused)", "https://youtu.be/FTCmwlfZ29A"),
            Alternative("Seated Cable Row (mid/upper back focused)", "https://youtu.be/Q-5V5T55giY"),
            Alternative("Chest Supported Machine Row", "https://youtu.be/iDiVxqvHGWY")
        ),
        "cable-lateral" to listOf(
            Alternative("Dumbbell Lateral Raises", "https://youtu.be/zcO3sgAeLA0"),
            Alternative("Lying Incline Lateral Raises", "https://youtu.be/upEqeI0F73M"),
            Alternative("Lean In Lateral Raise", "https://youtu.be/2q4kjTDg-vs"),
            Alternative("Wide Grip Barbell Upright Row (last resort)", "https://youtu.be/6BTMVh9AnCw")
        ),
        "overhead-rope" to listOf(
            Alternative("Incline Dumbbell Overhead Extensions", "https://youtu.be/3d86xMhHROA"),
            Alternative("Cable Pushdowns (elbow friendly)", "https://youtu.be/MlfCS_7ZLXA"),
            Alternative("Incline Barbell Skullcrushers", "https://youtu.be/XgwPiPY4vCI"),
            Alternative("Cross Cable Tricep Extensions", "https://youtu.be/Fua2QlXnn6Y")
        ),
        "pullups" to listOf(
            Alternative("(Weighted) Chin-Ups", "https://youtu.be/-TZRdvUS7Qo"),
            Alternative("Banded Pull-Ups", "https://youtu.be/VGm-f5-T5no"),
            Alternative("Inverted Row", "https://youtu.be/SyMSay4zrsA"),
            Alternative("Pull-Up Negatives", "https://youtu.be/SyMSay4zrsA"),
            Alternative("Kneeling Lat Pulldowns", "https://youtu.be/4LxKeTqlpZA"),
            Alternative("Lat Pulldowns", "https://youtu.be/AvYZZhEl7Xk")
        ),
        "leg-press" to listOf(
            Alternative("Barbell Back Squat", "https://youtu.be/Hj0dNZ9_LAE"),
            Alternative("Smith Machine Squat", "https://youtu.be/zSVi51Jp3eI"),
            Alternative("Barbell Back Box Squat (knee friendly)", "https://youtu.be/QryQO4VuPK8"),
            Alternative("Weighted Step-Ups (knee friendly)", "https://youtu.be/Cjc3AgmdtlA"),
            Alternative("Goblet Squat (lower back friendly)", "https://youtu.be/nYDEYFXN2Rs"),
            Alternative("Bulgarian Split Squat (quad-focused)", "https://youtu.be/r9XtxWSTlcg")
        ),
        "cable-fly" to listOf(
            Alternative("Standing Mid-Chest Cable Fly", "https://youtu.be/fyFVaCP9J-8"),
            Alternative("Pec-Deck Machine Fly", "https://youtu.be/rnV3y1P7894"),
            Alternative("Dumbbell Fly", "https://youtu.be/WRn2hqy0gXU"),
            Alternative("Banded Push-Ups", "https://youtu.be/dI7LVElfMOg")
        ),
        "calf-raise" to listOf(
            Alternative("Toes-Elevated Smith Machine Calf Raises", "https://youtu.be/_ChZv2iluM8"),
            Alternative("Single Leg Weighted Calf Raise", "https://youtu.be/cRKA_Qdut7I"),
            Alternative("Leg Press Calf Raises", "https://youtu.be/s8yUXsZrgE0")
        ),
        "hammer-curl" to listOf(
            Alternative("Rope Cable Curls (neutral grip)", "https://youtu.be/0vEzBCydrU0")
        ),
        "bulgarian" to listOf(
            Alternative("Heel Elevated Split Squat", "https://youtu.be/bJE0-eZLa6E"),
            Alternative("Walking Lunges (quad focused)", "https://youtu.be/JB20RuTOaFc"),
            Alternative("Reverse Lunges (knee friendly)", "https://youtu.be/AUEGDvCrQJA"),
            Alternative("Weighted Step Ups (knee friendly)", "https://youtu.be/Cjc3AgmdtlA")
        ),
        "chest-dips" to listOf(
            Alternative("Standing High To Low Cable Flyes", "https://youtu.be/JfZjng7jAKs"),
            Alternative("Decline Dumbbell Press", "https://youtu.be/pSOseCLdzIY"),
            Alternative("Decline Machine Chest Press", "https://youtu.be/H4R-LiTJeH8"),
            Alternative("(Banded) Incline Push-Ups", "https://youtu.be/67V0-5gjEeA")
        ),
        "leg-extension" to listOf(
            Alternative("Sissy Squat", "https://youtu.be/3SeCC8ABZ_Q"),
            Alternative("Heel Elevated Goblet Squat", "https://youtu.be/l9crMLuT4II"),
            Alternative("Reverse Lunges (knee friendly)", "https://youtu.be/AUEGDvCrQJA")
        ),
        "lat-row" to listOf(
            Alternative("Chest Supported Dumbbell Row (Lat-Focused)", "https://youtu.be/I2Unz9FR0sc"),
            Alternative("Barbell Row (lat focused)", "https://youtu.be/tS5lKXxtNvE"),
            Alternative("Half-Kneeling Cable Row", "https://youtu.be/tTev-zsqR4A"),
            Alternative("Rear Delt Cable Row", "https://youtu.be/k9G7BykDD4o")
        ),
        "rear-delt-fly" to listOf(
            Alternative("Chest Supported Dumbbell Rear Delt Row", "https://youtu.be/6LTUVaKpRCk"),
            Alternative("Barbell Row (mid/upper back focused)", "https://youtu.be/FTCmwlfZ29A")
        ),
        "pushdown" to listOf(
            Alternative("Overhead Rope Extensions", "https://youtu.be/7yoTblFCUQM"),
            Alternative("Incline Dumbbell Overhead Extensions", "https://youtu.be/3d86xMhHROA"),
            Alternative("Incline Barbell Skullcrushers", "https://youtu.be/XgwPiPY4vCI"),
            Alternative("Cross Cable Tricep Extensions", "https://youtu.be/Fua2QlXnn6Y")
        ),
        "shoulder-press" to listOf(
            Alternative("Standing Dumbbell Shoulder Press", "https://youtu.be/jWriqmLrQqs"),
            Alternative("Standing Barbell Overhead Press", "https://youtu.be/S3kYKH32VqI"),
            Alternative("Seated Neutral-Grip Dumbbell Shoulder Press (shoulder friendly)", "https://youtu.be/W35eREjZnhI"),
            Alternative("Half Kneeling Landmine Press (shoulder friendly)", "https://youtu.be/JOOS3MPCT8s")
        ),
        "incline-lateral" to listOf(
            Alternative("Dumbbell Lateral Raises", "https://youtu.be/zcO3sgAeLA0"),
            Alternative("Cable Lateral Raise", "https://youtu.be/1muit9qEctY"),
            Alternative("Lean In Lateral Raise", "https://youtu.be/2q4kjTDg-vs"),
            Alternative("Wide Grip Barbell Upright Row (last resort)", "https://youtu.be/6BTMVh9AnCw")
        ),
        "decline-pushup" to listOf(
            Alternative("Low Incline Dumbbell Press", "https://youtu.be/kpzUeELReEA"),
            Alternative("Incline Machine Chest Press", "https://youtu.be/abc1fisYB3w"),
            Alternative("Low Incline Smith Machine Press", "https://youtu.be/R53nThQcdZo"),
            Alternative("Low Incline Barbell Press", "https://youtu.be/jW4j7FoqudI"),
            Alternative("Low Incline Cable Press", "https://youtu.be/6qV1WZ_z0u0")
        ),
        "seated-row" to listOf(
            Alternative("Dumbbell Chest Supported Row (mid/upper back focused)", "https://youtu.be/kNvy2_9Ji2w"),
            Alternative("Barbell Row (mid/upper back focused)", "https://youtu.be/FTCmwlfZ29A"),
            Alternative("Chest Supported Machine Row", "https://youtu.be/iDiVxqvHGWY")
        ),
        "incline-curl" to listOf(
            Alternative("Behind Body Cable Curls", "https://youtu.be/S2CNDlAY8kY"),
            Alternative("Barbell Curl", "https://youtu.be/-ClfZ00zo8c")
        ),
        "calf-raise-d5" to listOf(
            Alternative("Toes-Elevated Smith Machine Calf Raises", "https://youtu.be/_ChZv2iluM8"),
            Alternative("Single Leg Weighted Calf Raise", "https://youtu.be/cRKA_Qdut7I"),
            Alternative("Leg Press Calf Raises", "https://youtu.be/s8yUXsZrgE0")
        )
    )

    val DAYS: List<DayPlan> = listOf(
        DayPlan(
            dayNumber = 1,
            title = "روز ۱",
            subtitle = "قدرت پایه + سینه + همسترینگ + پشت + جلو بازو",
            exercises = listOf(
                Exercise(
                    id = "back-squat",
                    name = "Barbell Back Squat",
                    persianName = "اسکوات هالتر از پشت",
                    defaultSets = 3,
                    reps = "6-8",
                    restSeconds = 150,
                    page = 6,
                    muscle = "پا",
                    note = "حالت پا را امتحان کن، حداقل تا موازی پایین برو و در صورت نیاز پاشنه‌ها را روی صفحه وزنه بالا بیاور.",
                    instructions = listOf(
                        "هالتر را روی بخش بالایی پشت قرار بده؛ استنس اولیه کمی بازتر از عرض شانه و پنجه‌ها حدود ۱۵ درجه بیرون.",
                        "میان‌تنه را محکم کن، با خم شدن هم‌زمان لگن و زانو پایین برو و میله را روی میدفوت نگه دار.",
                        "تا حداقل موازی یا کمی پایین‌تر برو و با حفظ بریسینگ بالا بیا."
                    )
                ),
                Exercise(
                    id = "low-incline-db-press",
                    name = "Low Incline Dumbbell Press",
                    persianName = "پرس دمبل شیب کم",
                    defaultSets = 3,
                    reps = "8-10",
                    restSeconds = 105,
                    page = 7,
                    muscle = "سینه",
                    note = "نیمکت را ۱ تا ۲ پله از پایین بالا بیاور، سینه بالا بماند و آرنج‌ها بیش از حد باز نشوند.",
                    instructions = listOf(
                        "زاویه نیمکت حدود ۱۵ تا ۳۰ درجه؛ کتف‌ها پایین و عقب و پاها محکم روی زمین.",
                        "دمبل‌ها را با آرنج حدود ۴۵ تا ۶۰ درجه از تنه پایین ببر.",
                        "با سینه فشار بده تا بازوها بالای شانه‌ها صاف شوند."
                    )
                ),
                Exercise(
                    id = "seated-leg-curl",
                    name = "Seated Leg Curl",
                    persianName = "پشت پا دستگاه نشسته",
                    defaultSets = 3,
                    reps = "10-15",
                    restSeconds = 105,
                    page = 8,
                    muscle = "پا",
                    note = "پنجه‌ها رو به بالا، حرکت کنترل‌شده و بدون قوس دادن کمر.",
                    instructions = listOf(
                        "زانو کنار محور دستگاه و پد پا کمی بالاتر از پاشنه تنظیم شود.",
                        "دسته‌ها را بگیر، میان‌تنه را محکم کن و پاشنه‌ها را با همسترینگ پایین بکش.",
                        "در بالا زانو را کاملاً قفل نکن؛ برگشت کنترل‌شده باشد."
                    )
                ),
                Exercise(
                    id = "lat-pulldown",
                    name = "Lat Pulldown",
                    persianName = "لت پول‌داون",
                    defaultSets = 3,
                    reps = "8-10",
                    restSeconds = 120,
                    page = 9,
                    muscle = "پشت",
                    note = "دست‌ها کمی بازتر از شانه، تنه کمی عقب و میله تا زیر چانه.",
                    instructions = listOf(
                        "ران‌ها زیر پد، گریپ دست از بالا و کمی بازتر از شانه.",
                        "بالای تنه کمی عقب؛ آرنج‌ها را پایین بکش تا میله نزدیک چانه برسد.",
                        "وزنه را کنترل‌شده بالا بده تا دست‌ها تقریباً صاف شوند."
                    )
                ),
                Exercise(
                    id = "behind-body-curl",
                    name = "Behind Body Cable Curl",
                    persianName = "جلو بازو سیم‌کش از پشت بدن",
                    defaultSets = 3,
                    reps = "10-12",
                    restSeconds = 105,
                    page = 10,
                    muscle = "بازو",
                    note = "کمی از کابل جلو برو، بازو پشت بدن آویزان بماند و از همان مسیر جمع شود.",
                    instructions = listOf(
                        "کابل در پایین، رو به بیرون بایست و دست کمی پشت تنه قرار گیرد.",
                        "شانه‌ها پایین و آرنج ثابت؛ دست را به سمت شانه جمع کن.",
                        "در بالا مسیر کابل را دنبال کن و سپس کامل کنترل‌شده باز کن."
                    )
                )
            )
        ),
        DayPlan(
            dayNumber = 2,
            title = "روز ۲",
            subtitle = "پرس سینه + هیپ هینج + پشت میانی + سرشانه + پشت بازو",
            exercises = listOf(
                Exercise(
                    id = "bench-press",
                    name = "Barbell Bench Press",
                    persianName = "پرس سینه هالتر",
                    defaultSets = 3,
                    reps = "6-8",
                    restSeconds = 150,
                    page = 14,
                    muscle = "سینه",
                    note = "دست‌ها کمی بازتر از شانه، سینه بالا، میله تا حدود خط سینه و آرنج‌ها کنترل‌شده.",
                    instructions = listOf(
                        "چشم‌ها زیر میله، گریپ متوسط کمی بازتر از شانه؛ کتف‌ها پایین و جمع.",
                        "میله را با آرنج ۴۵ تا ۶۰ درجه تا محدوده جناغ پایین بیاور.",
                        "بدون ضربه زدن به سینه، میله را بالا و کمی عقب فشار بده."
                    )
                ),
                Exercise(
                    id = "rdl",
                    name = "Barbell Romanian Deadlift",
                    persianName = "ددلیفت رومانیایی هالتر",
                    defaultSets = 3,
                    reps = "6-8",
                    restSeconds = 150,
                    page = 15,
                    muscle = "پا",
                    note = "باسن را عقب بده، زانو کمی خم و میله نزدیک بدن تا حدود ساق پایین بیاید.",
                    instructions = listOf(
                        "استنس عرض شانه، زانو کمی خم و میله نزدیک بدن.",
                        "با عقب دادن لگن پایین برو تا قبل از گرد شدن کمر؛ معمولاً زانو تا میانه ساق.",
                        "با فشار دادن لگن به جلو برگرد؛ پایین رفتن حدود ۲ تا ۳ ثانیه کنترل شود."
                    )
                ),
                Exercise(
                    id = "db-chest-row",
                    name = "Dumbbell Chest Supported Row",
                    persianName = "روئینگ دمبل با تکیه سینه",
                    defaultSets = 3,
                    reps = "10-12",
                    restSeconds = 105,
                    page = 16,
                    muscle = "پشت",
                    note = "نیمکت حدود ۳۰ درجه، آرنج‌ها ۴۵ تا ۶۰ درجه و کتف‌ها در بالا جمع شوند.",
                    instructions = listOf(
                        "نیمکت حدود ۳۰ درجه، سینه تکیه داده و دمبل‌ها زیر بدن.",
                        "کتف‌ها پایین، شکم و باسن فعال؛ آرنج‌ها ۴۵ تا ۶۰ درجه به عقب.",
                        "در بالا کتف‌ها را جمع کن و پایین رفتن را کنترل کن."
                    )
                ),
                Exercise(
                    id = "cable-lateral",
                    name = "Cable Lateral Raise",
                    persianName = "نشر جانب سیم‌کش",
                    defaultSets = 3,
                    reps = "15-20",
                    restSeconds = 105,
                    page = 17,
                    muscle = "سرشانه",
                    note = "دست در صفحه کتف بالا بیاید و به‌جای «بالا»، حرکت را «به بیرون» تصور کن.",
                    instructions = listOf(
                        "کابل پایین و پشت بدن؛ دست کنار ران.",
                        "بازو را با کمی خمیدگی حدود ۱۵ تا ۳۰ درجه جلوی بدن بالا بیاور.",
                        "تا ارتفاع شانه بالا برو و کنترل‌شده پایین بیاور."
                    )
                ),
                Exercise(
                    id = "overhead-rope",
                    name = "Overhead Rope Extensions",
                    persianName = "پشت بازو طناب بالای سر",
                    defaultSets = 3,
                    reps = "10-15",
                    restSeconds = 105,
                    page = 18,
                    muscle = "بازو",
                    note = "هر دست جدا، آرنج ثابت و دست در امتداد بازو به بالا و بیرون باز شود.",
                    instructions = listOf(
                        "یک سر طناب را بگیر، کابل پایین و پشت به دستگاه.",
                        "بازو بالای سر، آرنج ثابت؛ ساعد را کامل باز کن.",
                        "همان مسیر را کنترل‌شده برگردان و سپس سمت دیگر."
                    )
                )
            )
        ),
        DayPlan(
            dayNumber = 3,
            title = "روز ۳",
            subtitle = "کشش عمودی + چهارسر + سینه + ساق + جلو بازو",
            exercises = listOf(
                Exercise(
                    id = "pullups",
                    name = "Pull-Ups",
                    persianName = "بارفیکس",
                    defaultSets = 3,
                    reps = "6-12",
                    restSeconds = 150,
                    page = 21,
                    muscle = "پشت",
                    note = "دست‌ها کمی بازتر از شانه، چانه از میله رد شود؛ اگر ۶ تکرار پیوسته نمی‌رسی جایگزین انتخاب کن.",
                    instructions = listOf(
                        "گریپ از بالا کمی بازتر از شانه، بدن آویزان و شکم و باسن سفت.",
                        "شانه‌ها را پایین بیاور و آرنج‌ها را به سمت پهلوها و عقب بکش.",
                        "تا عبور چانه از میله بالا برو و سپس کنترل‌شده پایین بیا."
                    )
                ),
                Exercise(
                    id = "leg-press",
                    name = "Quad-Focused Leg Press",
                    persianName = "پرس پا با تمرکز چهارسر",
                    defaultSets = 3,
                    reps = "10-12",
                    restSeconds = 150,
                    page = 22,
                    muscle = "پا",
                    note = "جای پا پایین‌تر، زانوها جلو روی پنجه‌ها و پاشنه‌ها روی صفحه بمانند.",
                    instructions = listOf(
                        "پاها حدود عرض شانه و پایین‌تر روی صفحه؛ پنجه‌ها کمی بیرون.",
                        "زانوها را در مسیر پنجه‌ها خم کن و تا دامنه مناسب پایین برو.",
                        "با حفظ پاشنه‌ها روی صفحه فشار بده تا پاها تقریباً صاف شوند."
                    )
                ),
                Exercise(
                    id = "cable-fly",
                    name = "Seated Mid-Chest Cable Fly",
                    persianName = "فلای سیم‌کش نشسته وسط سینه",
                    defaultSets = 3,
                    reps = "10-15",
                    restSeconds = 105,
                    page = 23,
                    muscle = "سینه",
                    note = "کابل هم‌ارتفاع سینه، بازوها به هم نزدیک و در انتها مکث کوتاه.",
                    instructions = listOf(
                        "نیمکت نزدیک ۷۵ درجه و کابل‌ها هم‌ارتفاع وسط سینه.",
                        "کتف‌ها پایین و عقب؛ بازوها را جلو به هم نزدیک کن.",
                        "کمی مکث و سپس کنترل‌شده تا هم‌سطح شدن آرنج با تنه برگرد."
                    )
                ),
                Exercise(
                    id = "calf-raise",
                    name = "Standing Weighted Calf Raises",
                    persianName = "ساق ایستاده وزنه‌دار",
                    defaultSets = 3,
                    reps = "10-15",
                    restSeconds = 105,
                    page = 24,
                    muscle = "پا",
                    note = "پاها عرض لگن، پنجه‌ها روی صفحه و فشار اصلی روی شست پا.",
                    instructions = listOf(
                        "پاها عرض لگن و رو به جلو؛ وزنه با دمبل یا هالتر.",
                        "تا جای ممکن روی پنجه بالا برو، با تاکید فشار روی شست.",
                        "پاشنه را آرام پایین بیاور؛ در صورت تسلط پنجه‌ها را روی صفحه وزنه بالا بیاور."
                    )
                ),
                Exercise(
                    id = "hammer-curl",
                    name = "Hammer Curls",
                    persianName = "جلو بازو چکشی",
                    defaultSets = 3,
                    reps = "10-12",
                    restSeconds = 105,
                    page = 25,
                    muscle = "بازو",
                    note = "گریپ خنثی، آرنج ثابت و دمبل کمی به سمت جلوی بدن بالا بیاید.",
                    instructions = listOf(
                        "دمبل‌ها کنار بدن با گریپ خنثی.",
                        "با آرنج ثابت، هر دست را یکی‌درمیان کمی به سمت جلوی سینه جمع کن.",
                        "تا صاف شدن کامل بازو پایین بیاور و گریپ خنثی را حفظ کن."
                    )
                )
            )
        ),
        DayPlan(
            dayNumber = 4,
            title = "روز ۴",
            subtitle = "تک‌پا + دیپ + چهارسر + لت + پشت سرشانه + پشت بازو",
            exercises = listOf(
                Exercise(
                    id = "bulgarian",
                    name = "Bulgarian Split Squat (Glute-Focused)",
                    persianName = "اسپلیت اسکوات بلغاری با تمرکز باسن",
                    defaultSets = 3,
                    reps = "8-10 / leg",
                    restSeconds = 60,
                    page = 28,
                    muscle = "پا",
                    note = "فاصله پا بیشتر، تنه کمی جلو؛ هر پا را کامل کن، ۱ دقیقه استراحت و سپس سمت دیگر.",
                    instructions = listOf(
                        "پای عقب روی سطح بلند؛ پای جلو کمی دورتر برای تاکید بیشتر روی باسن.",
                        "تنه اندکی جلو و کمر خنثی؛ زانوی عقب را به زمین نزدیک کن.",
                        "بیشتر وزن روی پای جلو باشد و با پاشنه همان پا بالا بیا."
                    )
                ),
                Exercise(
                    id = "chest-dips",
                    name = "Chest Dips",
                    persianName = "دیپ سینه",
                    defaultSets = 3,
                    reps = "6-12",
                    restSeconds = 105,
                    page = 29,
                    muscle = "سینه",
                    note = "کمی به جلو متمایل شو، بدن را کنترل‌شده پایین بیاور و در دامنه راحت حرکت کن.",
                    instructions = listOf(
                        "روی پارالل قرار بگیر، بازوها قفل و تنه کمی جلو.",
                        "آرنج‌ها را به عقب خم کن و تا دامنه راحت پایین برو.",
                        "از کف دست‌ها فشار بده و با سینه به شروع برگرد."
                    )
                ),
                Exercise(
                    id = "leg-extension",
                    name = "Seated Leg Extensions",
                    persianName = "جلو پا دستگاه",
                    defaultSets = 3,
                    reps = "10-15",
                    restSeconds = 105,
                    page = 30,
                    muscle = "پا",
                    note = "در بالا حدود ۱ ثانیه مکث و برگشت وزنه کاملاً کنترل‌شده.",
                    instructions = listOf(
                        "زانو کنار محور دستگاه و پد کمی بالاتر از مچ.",
                        "با شکم محکم پاها را باز کن و زانوها رو به جلو بمانند.",
                        "در بالا مکث کوتاه و سپس کنترل‌شده پایین."
                    )
                ),
                Exercise(
                    id = "lat-row",
                    name = "Lat-Focused Cable Row",
                    persianName = "روئینگ سیم‌کش با تمرکز لت",
                    defaultSets = 3,
                    reps = "10-12",
                    restSeconds = 105,
                    page = 31,
                    muscle = "پشت",
                    note = "تنه کمی جلو، آرنج‌ها پایین و نزدیک پهلوها به سمت عقب کشیده شوند.",
                    instructions = listOf(
                        "زانو کمی خم، شانه‌ها پایین و تنه اندکی جلو.",
                        "آرنج‌ها را نزدیک پهلو به سمت جیب‌های عقب بکش.",
                        "تا رسیدن آرنج به سطح تنه بکش و سپس تقریباً تا صاف شدن بازوها برگرد."
                    )
                ),
                Exercise(
                    id = "rear-delt-fly",
                    name = "Rear Delt Cable Fly",
                    persianName = "فلای پشت سرشانه سیم‌کش",
                    defaultSets = 3,
                    reps = "10-15",
                    restSeconds = 105,
                    page = 32,
                    muscle = "سرشانه",
                    note = "کابل‌ها بالا، دست‌ها بدون دسته و تقریباً صاف؛ بازوها از بدن دور شوند.",
                    instructions = listOf(
                        "کابل‌ها در بالاترین نقطه و بدون دسته؛ کابل‌ها جلوی بدن ضربدری.",
                        "بازوهای تقریباً صاف را با زاویه حدود ۴۵ درجه پایین و عقب بکش.",
                        "کمی مکث و کنترل‌شده به شروع برگرد."
                    )
                ),
                Exercise(
                    id = "pushdown",
                    name = "Cable Pushdowns (Elbow Friendly)",
                    persianName = "پشت بازو سیم‌کش سازگارتر با آرنج",
                    defaultSets = 3,
                    reps = "8-10",
                    restSeconds = 105,
                    page = 33,
                    muscle = "بازو",
                    note = "اگر ممکن است دو طناب، آرنج ثابت و فشار رو به پایین و بیرون.",
                    instructions = listOf(
                        "کابل بالا؛ اگر ممکن است دو طناب برای طول بیشتر.",
                        "تنه حدود ۳۰ درجه جلو و آرنج‌ها نزدیک پهلو ثابت.",
                        "دست‌ها را پایین و بیرون باز کن و سپس تا حدود سینه برگرد."
                    )
                )
            )
        ),
        DayPlan(
            dayNumber = 5,
            title = "روز ۵",
            subtitle = "سرشانه + بالاسینه + پشت میانی + جلو بازو + ساق",
            exercises = listOf(
                Exercise(
                    id = "shoulder-press",
                    name = "Seated Dumbbell Shoulder Press",
                    persianName = "پرس سرشانه دمبل نشسته",
                    defaultSets = 3,
                    reps = "8-10",
                    restSeconds = 150,
                    page = 36,
                    muscle = "سرشانه",
                    note = "نیمکت ۲ تا ۳ پله از بالاترین حالت پایین؛ پرس در صفحه کتف.",
                    instructions = listOf(
                        "نیمکت حدود ۶۰ تا ۷۵ درجه؛ دمبل‌ها کنار شانه و آرنج کمی جلو.",
                        "شانه‌ها پایین، شکم سفت و دمبل‌ها را بالای شانه‌ها پرس کن.",
                        "تا حدود سطح چانه با آرنج حدود ۴۵ درجه پایین بیاور."
                    )
                ),
                Exercise(
                    id = "incline-lateral",
                    name = "Lying Incline Lateral Raises",
                    persianName = "نشر جانب خوابیده روی نیمکت شیبدار",
                    defaultSets = 3,
                    reps = "15-20",
                    restSeconds = 105,
                    page = 37,
                    muscle = "سرشانه",
                    note = "سینه روی نیمکت، آرنج کمی خم و دست‌ها در مسیر Y حدود ۱۵ تا ۳۰ درجه جلوی بدن.",
                    instructions = listOf(
                        "نیمکت حدود ۴۵ درجه و سینه روی پشتی؛ وزنه سبک.",
                        "بازوها کمی خم و در مسیر Y حدود ۱۵ تا ۳۰ درجه جلوی بدن بالا بروند.",
                        "تا ارتفاع شانه و سپس کنترل‌شده پایین."
                    )
                ),
                Exercise(
                    id = "decline-pushup",
                    name = "(Banded) Decline Push-Ups",
                    persianName = "شنا شیب منفی با کش",
                    defaultSets = 3,
                    reps = "10-20",
                    restSeconds = 105,
                    page = 38,
                    muscle = "سینه",
                    note = "کش روی پشت، پاها روی سطح بلند و آرنج‌ها حدود ۴۵ تا ۶۰ درجه.",
                    instructions = listOf(
                        "کش را مانند کوله روی پشت قرار بده و پاها را روی سطح بلند بگذار.",
                        "بدن را یکپارچه نگه دار و با آرنج ۴۵ تا ۶۰ درجه پایین برو.",
                        "تا نزدیک زمین پایین و سپس بالا و کمی عقب فشار بده."
                    )
                ),
                Exercise(
                    id = "seated-row",
                    name = "Seated Cable Row (Mid/Upper Back)",
                    persianName = "روئینگ سیم‌کش نشسته برای پشت میانی/بالایی",
                    defaultSets = 3,
                    reps = "10-12",
                    restSeconds = 105,
                    page = 39,
                    muscle = "پشت",
                    note = "اگر ممکن است دسته پهن، آرنج‌ها بازتر و کتف‌ها در انتها به هم نزدیک شوند.",
                    instructions = listOf(
                        "زانو کمی خم، کمر صاف و شانه‌ها پایین.",
                        "آرنج‌ها را ۴۵ تا ۶۰ درجه بازتر به عقب بکش و کتف‌ها را جمع کن.",
                        "وزنه را کنترل‌شده برگردان و اجازه بده کتف‌ها باز شوند، بدون گرد شدن زیاد کمر."
                    )
                ),
                Exercise(
                    id = "incline-curl",
                    name = "Incline Dumbbell Curl",
                    persianName = "جلو بازو دمبل روی نیمکت شیبدار",
                    defaultSets = 3,
                    reps = "8-10",
                    restSeconds = 105,
                    page = 40,
                    muscle = "بازو",
                    note = "نیمکت ۲ تا ۳ پله از بالا پایین؛ دست‌ها یکی‌درمیان و آرنج در جای خود ثابت.",
                    instructions = listOf(
                        "نیمکت حدود ۶۰ درجه، بازوها آویزان و کف دست ابتدا رو به داخل.",
                        "آرنج ثابت و دمبل را بالا جمع کن؛ کف دست در بالا رو به سقف بچرخد.",
                        "تا صاف شدن بازو پایین بیاور و مچ را به حالت خنثی برگردان."
                    )
                ),
                Exercise(
                    id = "calf-raise-d5",
                    name = "Standing Weighted Calf Raises",
                    persianName = "ساق ایستاده وزنه‌دار",
                    defaultSets = 3,
                    reps = "10-15",
                    restSeconds = 105,
                    page = 41,
                    muscle = "پا",
                    note = "پاها عرض لگن، پنجه‌ها روی صفحه و فشار اصلی روی شست پا.",
                    instructions = listOf(
                        "پاها عرض لگن و رو به جلو؛ وزنه با دمبل یا هالتر.",
                        "تا جای ممکن روی پنجه بالا برو، با تاکید فشار روی شست.",
                        "پاشنه را آرام پایین بیاور؛ در صورت تسلط پنجه‌ها را روی صفحه وزنه بالا بیاور."
                    )
                )
            )
        )
    )

    val ALL_EXERCISES: List<Exercise> by lazy {
        DAYS.flatMap { it.exercises }
    }

    fun findExercise(id: String): Exercise? {
        return ALL_EXERCISES.find { it.id == id }
    }

    fun getDay(dayNumber: Int): DayPlan {
        return DAYS.find { it.dayNumber == dayNumber } ?: DAYS[0]
    }
}

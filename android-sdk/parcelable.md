# Parcelable

предназначен для межпроцессорного взаимодействия

используется для трензакций через IBinder

не предназначен для сохранения в Persistent storage

занимает меньше место и в среднем быстрее кодируется и декодируется

можем менять имена полей

пакет и класс

значения полей

под капотом использует Reflection API

ClassLoader

TransactionTooLargeException

## Вопросы

если Bundle не предназначен для постоянного хранения то как восстанавливается экран и его данные после того как приложение было убито, а потом вернулись через таск менеждер

https://developer.android.com/guide/components/activities/parcelables-and-bundles#sdbp

The Binder transaction buffer has a limited fixed size, currently 1MB, which is shared by all transactions in progress for the process. Since this limit is at the process level rather than at the per activity level, these transactions include all binder transactions in the app such as onSaveInstanceState, startActivity and any interaction with the system. When the size limit is exceeded, a TransactionTooLargeException is thrown.

For the specific case of savedInstanceState, the amount of data should be kept small because the system process needs to hold on to the provided data for as long as the user can ever navigate back to that activity (even if the activity's process is killed). We recommend that you keep saved state to less than 50k of data.

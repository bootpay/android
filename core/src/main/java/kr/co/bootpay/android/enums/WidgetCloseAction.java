package kr.co.bootpay.android.enums;

/**
 * Widget 닫기 액션 타입 (iOS 패리티)
 * - FINISH_ACTIVITY: Activity.finish() 호출
 * - FRAGMENT_POP: FragmentManager에서 pop
 * - NONE: 앱에서 직접 처리
 */
public enum WidgetCloseAction {
    FINISH_ACTIVITY,
    FRAGMENT_POP,
    NONE
}

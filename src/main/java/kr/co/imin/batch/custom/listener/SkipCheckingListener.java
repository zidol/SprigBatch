package kr.co.imin.batch.custom.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class SkipCheckingListener extends StepExecutionListenerSupport {

    /**
     * 설명하면 StepExecutionListener 에서는 먼저 Step이 성공적으로 수행되었는지 확인하고
     * , StepExecution의 skip 횟수가 0보다 클 경우 COMPLETED WITH SKIPS 의 exitCode를 갖는 ExitStatus를 반환
     *
     * @param stepExecution {@link StepExecution} instance.
     * @return ExitStatus
     */
    public ExitStatus afterStep(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();
        if (!exitCode.equals(ExitStatus.FAILED.getExitCode()) &&
                stepExecution.getSkipCount() > 0) {
            return new ExitStatus("COMPLETED WITH SKIPS");
        }
        else {
            return null;
        }
    }
}

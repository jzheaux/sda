package regression;

public enum RegressionType {

    LINEAR_REGRESSION("linear regression"), RIDGE_REGRESSION("ridge_regression"), BINIOMIAL_LOGIT_REGRESSION("binomial_logit_regression");
    private String type;

    private RegressionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

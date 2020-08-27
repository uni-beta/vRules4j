package test.cases;

import java.io.Serializable;
import java.util.Date;

public class SearchPolicyInputVO implements Serializable {

    private Date dateLastModified;
	private String languageID;

	private String searchContext;

	private Date validityDate;

	private String policyNo;

	private String policyStatus;

	private String productCode;

	private String agentCode;

	private Date creationDateFrom;

	private Date creationDateTo;

	private Date policyIssueDateFrom;

	private Date policyIssueDateTo;

	private Date quotationIssueDateFrom;

	private Date quotationIssueDateTo;

	private Date policyEffectiveDateFrom;

	private Date policyEffectiveDateTo;

	private Date policyExpireDateFrom;

	private Date policyExpireDateTo;

	private String username;

	private String customerName;

	private String companyName;

	private Date dateofBirth;

	private String postCode;

	private Integer houseNumber;

	private String town;

	private String licensePlateNumber;

	private Integer maxListNumber;

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(Integer houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getLanguageID() {
		return languageID;
	}

	public void setLanguageID(String languageID) {
		this.languageID = languageID;
	}

	public String getLicensePlateNumber() {
		return licensePlateNumber;
	}

	public void setLicensePlateNumber(String licensePlateNumber) {
		this.licensePlateNumber = licensePlateNumber;
	}

	public Integer getMaxListNumber() {
		return maxListNumber;
	}

	public void setMaxListNumber(Integer maxListNumber) {
		this.maxListNumber = maxListNumber;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getSearchContext() {
		return searchContext;
	}

	public void setSearchContext(String searchContext) {
		this.searchContext = searchContext;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String zipCode) {
		this.postCode = zipCode;
	}

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}

	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}

	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public Date getDateLastModified() {
		return dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	public Date getDateofBirth() {
		return dateofBirth;
	}

	public void setDateofBirth(Date dateofBirth) {
		this.dateofBirth = dateofBirth;
	}

	public Date getPolicyEffectiveDateFrom() {
		return policyEffectiveDateFrom;
	}

	public void setPolicyEffectiveDateFrom(Date policyEffectiveDateFrom) {
		this.policyEffectiveDateFrom = policyEffectiveDateFrom;
	}

	public Date getPolicyEffectiveDateTo() {
		return policyEffectiveDateTo;
	}

	public void setPolicyEffectiveDateTo(Date policyEffectiveDateTo) {
		this.policyEffectiveDateTo = policyEffectiveDateTo;
	}

	public Date getPolicyExpireDateFrom() {
		return policyExpireDateFrom;
	}

	public void setPolicyExpireDateFrom(Date policyExpireDateFrom) {
		this.policyExpireDateFrom = policyExpireDateFrom;
	}

	public Date getPolicyExpireDateTo() {
		return policyExpireDateTo;
	}

	public void setPolicyExpireDateTo(Date policyExpireDateTo) {
		this.policyExpireDateTo = policyExpireDateTo;
	}

	public Date getPolicyIssueDateFrom() {
		return policyIssueDateFrom;
	}

	public void setPolicyIssueDateFrom(Date policyIssueDateFrom) {
		this.policyIssueDateFrom = policyIssueDateFrom;
	}

	public Date getPolicyIssueDateTo() {
		return policyIssueDateTo;
	}

	public void setPolicyIssueDateTo(Date policyIssueDateTo) {
		this.policyIssueDateTo = policyIssueDateTo;
	}

	public Date getQuotationIssueDateFrom() {
		return quotationIssueDateFrom;
	}

	public void setQuotationIssueDateFrom(Date quotationIssueDateFrom) {
		this.quotationIssueDateFrom = quotationIssueDateFrom;
	}

	public Date getQuotationIssueDateTo() {
		return quotationIssueDateTo;
	}

	public void setQuotationIssueDateTo(Date quotationIssueDateTo) {
		this.quotationIssueDateTo = quotationIssueDateTo;
	}

	public Date getValidityDate() {
		return validityDate;
	}

	public void setValidityDate(Date validityDate) {
		this.validityDate = validityDate;
	}

  
}

package de.vksi.c4j.eclipse.plugin.util.requestor;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class AssosiatedMemberRequest
{
    public static Builder newCorrespondingMemberRequest()
    {
        return new Builder();
    }

    private Builder builder;

    private AssosiatedMemberRequest(Builder builder)
    {
        this.builder = builder;
    }

    public MethodDeclaration getCurrentMethodDeclaration()
    {
    	return builder.currentMethodDeclaration;
    }

    public IMethod getCurrentMethod()
    {
        return builder.currentMethod;
    }

    public String getPromptText()
    {
        return builder.promptText;
    }

    public boolean shouldReturn(MemberType memberType)
    {
        return builder.expectedMemberType == memberType;
    }

    public static enum MemberType
    {
        TYPE, METHOD
    }
    
	public boolean isCreateRequest() {
		return builder.isCreateRequest;
	}

    public static final class Builder
    {
        private boolean isCreateRequest;
        private MethodDeclaration currentMethodDeclaration;
        private IMethod currentMethod;
        private String promptText;
        private MemberType expectedMemberType = MemberType.METHOD;

        private Builder()
        {
        }
        
        public Builder asCreateRequest()
        {
            this.isCreateRequest = true;
            return this;
        }

        public Builder setDialogPromtText(String promptText)
        {
            this.promptText = promptText;
            return this;
        }

        public Builder withCurrentMethodDeclaration(MethodDeclaration method)
        {
        	currentMethodDeclaration = method; 
        	withCurrentMethod((IMethod) method.resolveBinding().getJavaElement());
         
            return this;
        }

        public Builder withCurrentMethod(IMethod method)
        {
        	currentMethod = method;
        	return this;
        }

        public Builder withExpectedResultType(MemberType memberType)
        {
            this.expectedMemberType = memberType;
            return this;
        }

        public AssosiatedMemberRequest build()
        {
            return new AssosiatedMemberRequest(this);
        }
    }
}

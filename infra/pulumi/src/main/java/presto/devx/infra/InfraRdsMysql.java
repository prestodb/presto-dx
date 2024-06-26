package presto.devx.infra;

import com.pulumi.aws.ec2.SecurityGroup;
import com.pulumi.aws.rds.Instance;
import com.pulumi.aws.rds.InstanceArgs;
import com.pulumi.aws.rds.SubnetGroup;
import com.pulumi.aws.rds.SubnetGroupArgs;
import com.pulumi.awsx.ec2.Vpc;
import com.pulumi.core.Output;

/**
 *
 * @author linsong
 */
public class InfraRdsMysql {

    private final Instance instance;

    public InfraRdsMysql(Output<String> password, Vpc vpc, SecurityGroup sg) {
        SubnetGroup subnetGroup = new SubnetGroup("presto-devx-infra-rds-subnet-group",
                SubnetGroupArgs.builder().subnetIds(vpc.publicSubnetIds()).build());
        String name = "presto-devx-infra-mysql";
        instance = new Instance(name, InstanceArgs.builder()
                .allocatedStorage(50)
                .backupRetentionPeriod(7)
                .dbSubnetGroupName(subnetGroup.name())
                .engine("mysql")
                .engineVersion("8.0")
                .instanceClass("db.r6g.large")
                .maxAllocatedStorage(500)
                .dbName(name.replace('-', '_'))
                .password(password)
                .performanceInsightsEnabled(true)
                .publiclyAccessible(true)
                .skipFinalSnapshot(true)
                .tags(App.TAGS)
                .username("presto")
                .vpcSecurityGroupIds(Output.all(sg.id()))
                .build());
    }

    public Instance getInstance() {
        return instance;
    }
}

package com.welfare;

import com.welfare.eumn.RelationEnum;
import org.springframework.stereotype.Component;

/**
 * 模拟计算项目信任值过程，所以涉及到的数值以实际情况为主。
 */
@Component
public class TrustMain {
    // 1.用于判断当前用户与项目发起人的关系
    public float is_relation(RelationEnum relationEnum) {
        //判断当前用户与发起人的关系
        switch (relationEnum) {
            case family:
                return 1.0f;
            case relative:
                return 0.8f;
            case friend:
                return 0.6f;
            case lover:
                return 0.2f;
            default:
                return 0.4f;
        }
    }

    // 2.用于计算项目募捐金额和评估金额之间的差距（用于计算状态信任值）
    public float is_disparity(int donation_amout) {
        //这里假设评估金额为50000
        int assessment = 50000;
        int disparity = donation_amout - assessment;
        //状态信任值的变化值
        float state_change = 0;
        if (disparity <= 10000) {
            return state_change;
        } else if (disparity <= 20000) {
            state_change = 0.1f;
        } else if (disparity <= 40000) {
            state_change = 0.2f;
        } else if (disparity <= 60000) {
            state_change = 0.3f;
        } else if (disparity <= 80000) {
            state_change = 0.4f;
        } else {
            state_change = 0.5f;
        }
        return state_change;
    }

    // 3.用于计算捐款金额权重
    public float is_weight(int donation_amout) {
        float amount_weight = 0;
        if (donation_amout < 50) {
            amount_weight = 0.2f;
        } else if (donation_amout < 100) {
            amount_weight = 0.4f;
        } else if (donation_amout < 300) {
            amount_weight = 0.6f;
        } else if (donation_amout < 500) {
            amount_weight = 0.8f;
        } else {
            amount_weight = 1.0f;
        }
        return amount_weight;
    }

    // 4. 用户计算聚合信任值
    public float cal_togetherTrust(int times, float role_value, float amount_weight, float state_value) {
        // 初始化聚合信任值
        float t_trust_value = 0;
        // 项目捐款阈值
        int H = 3;
        float d_trust_value = amount_weight * (state_value + role_value) / times;
        d_trust_value = (float) (Math.round(d_trust_value * 100.0) / 100.0);

        // 4.计算聚合信任值
        // 初始化聚合信任值

        // 4.1 当前用户向该项目的捐款次数大于等于阈值时，聚合信任值 = 直接信任值
        if (times >= H) {
            t_trust_value = d_trust_value;
        }
        // 4.2 当用户向该项目的捐款次数小于阈值时，聚合信任值 = 直接信任值 + 推荐信任值
        else {

            // 现在假设用户user_i和用户r1以及用户r2在当前项目互为推荐关系
            // 即user_i和r1以及r2都给当前项目捐款过
            // 假设r1和当前项目发起人的关系是“家人”
            // 假设r2和当前项目发起人的关系是“爱心人士”

            // 4.2.1 计算推荐信任值（推荐信任值 = 交易密度因子×相似度因子）
            // 交易密度调节常数
            float td_c = 0.5f;
            // 直接信任的权重因子
            float d_w = 0.6f;
            // 推荐信任的权重因子
            float r_w = (float) (1.0 - d_w);
            // 假设用户user_i参与的所有项目个数为6
            int i_q = 6;
            // 假设用户user_i和r1共同参与项目的个数为3
            int i_r1_p = 3;

            // 4.2.1.1 计算user_i和r1的推荐信任值
            // 假设这三个项目为A，B，C;A为当前项目
            // 已知user_i与A项目发起人的关系为“亲戚”，r1与A项目发起人的关系为“家人”
            float r1_a_relation = 1.0f;
            // 假设user_i与B项目发起人的关系为“朋友”，与C项目发起人的关系为“家人”
            float useri_b_relation = 0.6f;
            float useri_c_relation = 1.0f;
            // 假设r1与B项目发起人的关系为“同事”，与C项目发起人的关系为“爱心人士”
            float r1_b_relation = 0.4f;
            float r1_c_relation = 0.2f;

            // 计算用户user_i和r1的交易密度因子
            float freq_ir1 = (float) ((i_r1_p / (i_q * 1.0)) * Math.pow(td_c, (1 / (i_r1_p * 1.0))));
            freq_ir1 = (float) (Math.round(freq_ir1 * 100.0) / 100.0);
            // 计算用户user_i和r1的相似度因子
            // 分子
            float sim_ir1_up = (role_value * r1_a_relation) + (useri_b_relation * r1_b_relation)
                    + (useri_c_relation * r1_c_relation);
            // 分母中的乘号左边
            float sim_ir1_ldown = (float) (Math.pow(role_value, 2) + Math.pow(useri_b_relation, 2) + Math.pow(useri_c_relation, 2));
            sim_ir1_ldown = (float) Math.pow(sim_ir1_ldown, (1 / 2.0));
            // 分母中的乘号右边
            float sim_ir1_rdown = (float) (Math.pow(r1_a_relation, 2) + Math.pow(r1_b_relation, 2) + Math.pow(r1_c_relation, 2));
            sim_ir1_rdown = (float) Math.pow(sim_ir1_rdown, (1 / 2.0));
            // 分母
            float sim_ir1_down = sim_ir1_ldown * sim_ir1_rdown;
            // user_i和r1的相似度因子
            float sim_ir1 = sim_ir1_up / sim_ir1_down;
            sim_ir1 = (float) (Math.round(sim_ir1 * 100.0) / 100.0);
            // user_i和r1的推荐因子
            float b_ir1 = freq_ir1 + sim_ir1;
            b_ir1 = (float) (Math.round(b_ir1 * 100.0) / 100.0);


            // 4.2.1.2 同理计算user_i和r2的推荐信任值
            // 假设用户user_i和r2共同参与项目的个数为1
            int i_r2_p = 1;
            // 显然user_i和r2共同参与的项目只有当前项目（A项目）
            // 假设r2和A项目发起人的关系是“邻居”
            float r2_a_relation = 0.2f;

            // 计算user_i和r2的交易密度因子
            float freq_ir2 = (float) ((i_r2_p / (i_q * 1.0)) * Math.pow(td_c, (1 / (i_r2_p * 1.0))));
            freq_ir2 = (float) (Math.round(freq_ir2 * 100.0) / 100.0);

            // 同理计算user_i和r2的相似度因子
            // 分子
            float sim_ir2_up = (role_value * r2_a_relation);
            // 分母中乘号左边
            float sim_ir2_ldown = (float) (Math.pow(role_value, 2));
            sim_ir2_ldown = (float) Math.pow(sim_ir2_ldown, (1 / 2.0));
            // 分母中乘号右边
            float sim_ir2_rdown = (float) (Math.pow(r2_a_relation, 2));
            sim_ir2_rdown = (float) Math.pow(sim_ir2_rdown, (1 / 2.0));
            // 分母
            float sim_ir2_down = sim_ir2_ldown * sim_ir2_rdown;
            // user_i和r2的相似度因子
            float sim_ir2 = sim_ir2_up / sim_ir2_down;
            sim_ir2 = (float) (Math.round(sim_ir2 * 100.0) / 100.0);
            // user_i和r1的推荐因子
            float b_ir2 = freq_ir2 + sim_ir2;
            b_ir2 = (float) (Math.round(b_ir2 * 100.0) / 100.0);

            // 计算聚合信任值
            // 计算加号左边部分(直接信用权重因子 × 直接信用值)
            float t_lvalue = d_w * d_trust_value;
            // 计算加号右边部分
            // 这一过程需要用到r1和r2的直接信用值
            // 假设r1的直接信用值为0.6，r2的直接信用值为0.7
            float r1_d_v = 0.6f;
            float r2_d_v = 0.7f;
            // 计算分子部分
            float t_rvalue_up = b_ir1 * r1_d_v + b_ir2 * r2_d_v;
            // 计算分母部分
            float t_rvalue_down = b_ir1 + b_ir2;
            // 分子/分母 * 推荐权重因子
            float t_rvalue = r_w * (t_rvalue_up / t_rvalue_down);
            // 左边 + 右边
            t_trust_value = t_lvalue + t_rvalue;
        }
        return t_trust_value;
    }

    public static void main(String[] args) {
        TrustMain trustMain = new TrustMain();

        // 初始化项目状态信任值(0.5)
        float state_value = 0.5f;
        // 项目的募捐金额
        int donation_amout = 60000;


        //当前用户（捐款人）
        String user_i = "Conan";

        // 1.根据当前用户user_i与项目发起人的关系计算角色信任值;
        // 假设用户user_i与当前项目发起人的关系是“亲戚”;
        int relation = 2;
        RelationEnum relationEnum = RelationEnum.valueToEnum(relation);
        float role_value = trustMain.is_relation(relationEnum);

        // 2.根据项目的募捐金额和评估金额计算状态信任值
        float state_change = trustMain.is_disparity(donation_amout);
        state_value -= state_change;

        // 3.计算聚合信用值,即项目的信任值（保留两位小数）
        // 计算捐款金额权重
        float amount_weight = trustMain.is_weight(donation_amout);
        //当前用户向该项目的捐款次数
        int times = 2;
        float t_trust_value = trustMain.cal_togetherTrust(times, role_value, amount_weight, state_value);
        t_trust_value = (float) (Math.round(t_trust_value * 100.0) / 100.0);
        System.out.println("聚合信用值:" + t_trust_value);
    }
}
